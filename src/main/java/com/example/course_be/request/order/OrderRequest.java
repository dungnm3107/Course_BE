package com.example.course_be.request.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private Long userId;
    private Long courseId;
    private BigDecimal totalPrice;
    private String status;
    private String paymentMethod;
}
