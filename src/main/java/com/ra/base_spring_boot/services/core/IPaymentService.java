package com.ra.base_spring_boot.services.core;

import com.ra.base_spring_boot.dto.req.OrderReq;
import com.ra.base_spring_boot.model.constants.PaymentStatus;
import org.json.JSONObject;

public interface IPaymentService {
    JSONObject createOrder(OrderReq req) throws Exception;
    JSONObject getOrderStatus(String apptransid) throws Exception;
    void changePaymentStatus(String transId, PaymentStatus status, Long providerTransId);
}
