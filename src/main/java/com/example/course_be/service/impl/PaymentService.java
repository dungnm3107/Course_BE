package com.example.course_be.service.impl;

import com.example.course_be.config.VNPAYConfig;
import com.example.course_be.response.PaymentDTO;
import com.example.course_be.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final VNPAYConfig vnPayConfig;

    public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request) {
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L; // Convert to cents
        String bankCode = request.getParameter("bankCode");
        String orderId = request.getParameter("orderId");
        String description = request.getParameter("description");



        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        vnpParamsMap.put("vnp_TxnRef", String.valueOf(orderId));
        vnpParamsMap.put("vnp_OrderInfo",String.valueOf(description));

        // Add bankCode if available
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }

        // Add client IP
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        // Generate query URL and hash data
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true); // Encodes key=value for URL
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false); // No encoding for hash

        // Create the signature using hmacSHA512
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);

        // Add the signature to the query URL
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;

        // Create the full payment URL
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;

        // Return the payment URL
        return PaymentDTO.VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl)
                .build();
    }
}



