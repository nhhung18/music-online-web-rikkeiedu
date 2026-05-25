package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.req.OrderReq;
import com.ra.base_spring_boot.services.core.impl.ZaloPayServiceImpl;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final ZaloPayServiceImpl zaloPayService;

    @PostMapping("/zalopay")
    public ResponseEntity<?> payWithZalo(@RequestBody OrderReq req){
        try {
            JSONObject result = zaloPayService.createOrder(req);
            return ResponseEntity.ok(result.toMap());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("{apptransid}/order-status")
    public ResponseEntity<?> getOrderStatus(@PathVariable String apptransid){
        try {
            JSONObject result = zaloPayService.getOrderStatus(apptransid);
            return ResponseEntity.ok(result.toMap());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{transId}/refund")
    public ResponseEntity<?> refund(@PathVariable String transId){
        try{
            JSONObject res=zaloPayService.refund(transId);
            return ResponseEntity.ok(res.toString());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
