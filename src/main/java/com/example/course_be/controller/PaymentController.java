package com.example.course_be.controller;


import com.example.course_be.response.PaymentDTO;
import com.example.course_be.service.OrderService;
import com.example.course_be.service.impl.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.course_be.response.ResponseObject;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;
    @GetMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(HttpServletRequest request) {
        return new ResponseObject<>(HttpStatus.OK, "Success", paymentService.createVnPayPayment(request));
    }
    @GetMapping("/vn-pay/callback")
    public ResponseEntity<?> handleVNPayReturn(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        Map<String, String> vnpParams = new HashMap<>();
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            vnpParams.put(entry.getKey(), entry.getValue()[0]);
        }
        String vnpResponseCode = vnpParams.get("vnp_ResponseCode");
        String vnpTxnRef = vnpParams.get("vnp_TxnRef");
        System.out.println(vnpTxnRef);

//        Long orderId = Long.valueOf(vnpTxnRef);
        // Kiểm tra nếu vnp_TxnRef là null hoặc trống
        if (vnpTxnRef == null || vnpTxnRef.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Transaction reference (vnp_TxnRef) is missing or invalid.");
        }

        Long orderId;
        try {
            // Chuyển đổi vnp_TxnRef thành Long
            orderId = Long.valueOf(vnpTxnRef);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Transaction reference (vnp_TxnRef) is not a valid number.");
        }
        // Kiểm tra mã phản hồi từ VNPAY
        if ("00".equals(vnpResponseCode)) {
            orderService.updateOrderStatus(orderId, true);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "http://localhost:5173/payment-success")
                    .build();
        } else {
            orderService.updateOrderStatus(orderId, false);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "http://localhost:5173/payment-failed")
                    .build();
        }
    }


}

