package com.example.course_be.controller;

import com.example.course_be.request.order.CreateOrderRequest;
import com.example.course_be.service.impl.ZaloPayService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/zalopay")
public class ZalopayController {

    private final ZaloPayService zaloPayService;

    public ZalopayController(ZaloPayService zaloPayService) {
        this.zaloPayService = zaloPayService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<String> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        try {
            String result = zaloPayService.createOrder(createOrderRequest);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
        }
    }
}
