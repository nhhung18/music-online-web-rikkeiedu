package com.ra.base_spring_boot.controller;
import com.ra.base_spring_boot.config.ZaloPayConfig;
import com.ra.base_spring_boot.model.constants.PaymentStatus;
import com.ra.base_spring_boot.services.core.IPaymentService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@RestController
@RequiredArgsConstructor
public class CallbackController {
    private Mac HmacSHA256;
    private final IPaymentService paymentService;

    @PostConstruct
    public void init() throws Exception {
        HmacSHA256 = Mac.getInstance("HmacSHA256");
        HmacSHA256.init(new SecretKeySpec(ZaloPayConfig.KEY2.getBytes(), "HmacSHA256"));
    }

    @PostMapping("/callback")
    public String callback(@RequestBody String jsonStr) {
        JSONObject result = new JSONObject();

        try {
            JSONObject cbdata = new JSONObject(jsonStr);
            String dataStr = cbdata.getString("data");
            String reqMac = cbdata.getString("mac");

            byte[] hashBytes = HmacSHA256.doFinal(dataStr.getBytes(StandardCharsets.UTF_8));
            String mac = HexFormat.of().formatHex(hashBytes);

            if (!reqMac.equals(mac)) {
                result.put("returncode", -1);
                result.put("returnmessage", "mac not equal");
            } else {
                JSONObject data = new JSONObject(dataStr);
                String apptransid = data.getString("apptransid");
                Long zptransid = data.getLong("zptransid");
                int orderStatus = data.getInt("orderstatus");

                if (orderStatus == 1) {
                    paymentService.changePaymentStatus(apptransid, PaymentStatus.COMPLETED, zptransid);
                } else {
                    paymentService.changePaymentStatus(apptransid, PaymentStatus.FAILED, zptransid);
                }

                result.put("returncode", 1);
                result.put("returnmessage", "success");
            }
        } catch (Exception ex) {
            result.put("returncode", 0); // ZaloPay server sẽ callback lại (tối đa 3 lần)
            result.put("returnmessage", ex.getMessage());
        }

        return result.toString();
    }
}
