package com.ra.base_spring_boot.services.core.impl;

import com.ra.base_spring_boot.config.ZaloPayConfig;
import com.ra.base_spring_boot.dto.req.OrderReq;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Payment;
import com.ra.base_spring_boot.model.SubscriptionPlan;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.PaymentMethod;
import com.ra.base_spring_boot.model.constants.PaymentStatus;
import com.ra.base_spring_boot.repository.IPaymentRepository;
import com.ra.base_spring_boot.repository.ISubscriptionPlanRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.core.IPaymentService;
import com.ra.base_spring_boot.services.core.ISubscriptionService;
import com.ra.base_spring_boot.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vn.zalopay.crypto.HMACUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ZaloPayServiceImpl implements IPaymentService {
    private final ISubscriptionPlanRepository subscriptionPlanRepository;
    private final IUserRepository userRepository;
    private final IPaymentRepository paymentRepository;
    private final ISubscriptionService subscriptionService;

    @Override
    public JSONObject createOrder(OrderReq req) throws Exception {
        SubscriptionPlan subscriptionPlan=subscriptionPlanRepository.findById(req.getPlanId())
                .orElseThrow(()-> new HttpNotFound("Plan not found"));

        Long id = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, String> embeddata = new HashMap<>(){{
            put("merchantinfo", "eshop123");
            put("promotioninfo", "");
            put("redirecturl", ZaloPayConfig.REDIRECT_URL);
        }};

        Map<String, Object> item = new HashMap<>(){{
                    put("itemid", subscriptionPlan.getId());
                    put("itemname", subscriptionPlan.getPlanName());
                    put("itemprice", subscriptionPlan.getPrice().intValue());
                    put("itemquantity", 1);
        }};

        JSONArray items = new JSONArray();
        items.put(new JSONObject(item));


        String apptransid=getCurrentTimeString("yyMMdd") +"_"+ new Date().getTime();

        Map<String, Object> order = new HashMap<>(){{
            put("appid", ZaloPayConfig.APP_ID);
            put("apptransid", apptransid);
            put("apptime", System.currentTimeMillis());
            put("appuser", user.getId().toString());
            put("amount", subscriptionPlan.getPrice().intValue());
            put("description", "Payment for subscription");
            put("bankcode", "");
            put("item", items.toString());
            put("embeddata", new JSONObject(embeddata).toString());
        }};

        // appid +”|”+ apptransid +”|”+ appuser +”|”+ amount +"|" + apptime +”|”+ embeddata +"|" +item
        String data = order.get("appid") +"|"+ order.get("apptransid") +"|"+ order.get("appuser") +"|"+ order.get("amount")
                +"|"+ order.get("apptime") +"|"+ order.get("embeddata") +"|"+ order.get("item");
        order.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZaloPayConfig.KEY1, data));

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(ZaloPayConfig.END_POINT_CREATE);

        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> e : order.entrySet()) {
            params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
        }

        // Content-Type: application/x-www-form-urlencoded
        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        JSONObject zaloResponse = new JSONObject(resultJsonStr.toString());

        if (zaloResponse.getInt("returncode") == 1 ) {
            Payment payment= Payment.builder()
                    .user(user)
                    .subscriptionPlan(subscriptionPlan)
                    .transactionId(apptransid)
                    .amount(subscriptionPlan.getPrice())
                    .paymentMethod(PaymentMethod.ZALO_PAY)
                    .paymentStatus(PaymentStatus.PENDING)
                    .build();

            paymentRepository.save(payment);
        }

        return zaloResponse;
    }

    @Override
    public void changePaymentStatus(String transId, PaymentStatus newStatus, Long providerTransId) {
        Payment payment= paymentRepository.findByTransactionId(transId)
                .orElseThrow(()-> new HttpNotFound("Payment not found"));
        PaymentStatus currentStatus = payment.getPaymentStatus();
        if (currentStatus == PaymentStatus.COMPLETED && newStatus == PaymentStatus.COMPLETED) {
            return;
        }
        if (currentStatus == PaymentStatus.REFUNDED) {
            return;
        }
        if (currentStatus == PaymentStatus.COMPLETED && newStatus == PaymentStatus.FAILED) {
            return;
        }
        payment.setPaymentStatus(newStatus);
        payment.setProviderTransId(providerTransId);
        paymentRepository.save(payment);
        if (newStatus == PaymentStatus.COMPLETED) {
            subscriptionService.addOrExtend(payment.getSubscriptionPlan().getId(), payment.getUser().getId());
        } else if (newStatus == PaymentStatus.REFUNDED) {

        }
    }


    public static String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }

    @Override
    public JSONObject getOrderStatus(String apptransid) throws Exception{
        String data = ZaloPayConfig.APP_ID +"|"+ apptransid  +"|"+ ZaloPayConfig.KEY1; // appid|apptransid|key1
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZaloPayConfig.KEY1, data);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("appid", ZaloPayConfig.APP_ID));
        params.add(new BasicNameValuePair("apptransid", apptransid));
        params.add(new BasicNameValuePair("mac", mac));

        URIBuilder uri = new URIBuilder(ZaloPayConfig.END_POINT_GET_STATUS);
        uri.addParameters(params);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(uri.build());

        CloseableHttpResponse res = client.execute(get);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        return new JSONObject(resultJsonStr.toString());
    }

    @Scheduled(fixedRate = 15000)
    public void checkPendingPayment(){
        List<Payment> pendingPayments=paymentRepository.findByPaymentStatus(PaymentStatus.PENDING);
        for(Payment payment: pendingPayments){
            LocalDateTime createAt=payment.getCreatedAt();
            long diffMinutes= Duration.between(createAt,LocalDateTime.now()).toMinutes();

            if(diffMinutes>15){
                changePaymentStatus(payment.getTransactionId(), PaymentStatus.FAILED, null);
                continue;
            }

            try{
                JSONObject statusRes=getOrderStatus(payment.getTransactionId());
                int returnCode=statusRes.getInt("returncode");

                if(returnCode==1){
                    changePaymentStatus(payment.getTransactionId(), PaymentStatus.COMPLETED,statusRes.getLong("zptransid"));
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public JSONObject refund(String transId) throws Exception {
        Payment payment = paymentRepository.findByTransactionId(transId)
                .orElseThrow(() -> new HttpNotFound("Payment not found"));
        if (payment.getProviderTransId() == null) {
            throw new RuntimeException("No ZaloPay transaction id (zptransid) found for refund");
        }
        String appid = ZaloPayConfig.APP_ID;
        Random rand = new Random();
        long timestamp = System.currentTimeMillis(); // miliseconds
        String uid = timestamp + "" + (111 + rand.nextInt(888)); // unique id

        Map<String, Object> order = new HashMap<String, Object>(){{
            put("appid", appid);
            put("zptransid", payment.getProviderTransId());
            put("mrefundid", getCurrentTimeString("yyMMdd") +"_"+ appid +"_"+uid);
            put("timestamp", timestamp);
            put("amount", payment.getAmount());
            put("description", "Hoàn tiền giao dịch "+payment.getTransactionId());
        }};

        // appid|zptransid|amount|description|timestamp
        String data = order.get("appid") +"|"+ order.get("zptransid") +"|"+ order.get("amount")
                +"|"+ order.get("description") +"|"+ order.get("timestamp");
        order.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZaloPayConfig.KEY1, data));

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(ZaloPayConfig.END_POINT_PARTIAL_REFUND);

        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> e : order.entrySet()) {
            params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
        }

        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        changePaymentStatus(transId, PaymentStatus.REFUNDED, null);
        return new JSONObject(resultJsonStr.toString());
    }
}
