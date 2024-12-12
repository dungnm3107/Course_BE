package com.example.course_be.service;

import com.example.course_be.entity.Course;
import com.example.course_be.entity.Order;
import com.example.course_be.request.order.OrderRequest;
import com.example.course_be.response.Order.OrderResponse;
import com.example.course_be.response.Order.RevenueResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(OrderRequest orderRequest);

    OrderResponse getOrderById(Long orderId);
    void updateOrderStatus(Long orderId, boolean isPaymentSuccess);
    Optional<OrderResponse> getLatestOrderByUserIdAndCourseId(Long userId, Long courseId);
    List<Course> getMostPurchasedCourses();
    List<RevenueResponse> getMonthlyRevenue(int year);
    BigDecimal getTotalRevenueByStatusCompleted();

    List<OrderResponse> getOrdersByUserId(Long userId);

}

