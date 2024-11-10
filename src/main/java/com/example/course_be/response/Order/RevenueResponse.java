package com.example.course_be.response.Order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueResponse {
    private Integer month;
    private BigDecimal totalRevenue;
}
