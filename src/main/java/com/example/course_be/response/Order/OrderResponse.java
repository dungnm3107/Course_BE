package com.example.course_be.response.Order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private Long courseId;
    private BigDecimal totalPrice;
    private String status;
    private String paymentMethod;
}
