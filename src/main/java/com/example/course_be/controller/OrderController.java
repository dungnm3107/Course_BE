package com.example.course_be.controller;


import com.example.course_be.entity.Course;
import com.example.course_be.entity.Order;
import com.example.course_be.request.order.OrderRequest;
import com.example.course_be.response.Order.OrderResponse;
import com.example.course_be.response.Order.RevenueResponse;
import com.example.course_be.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            logger.info("Received request to create order: {}", orderRequest);
            Order createdOrder = orderService.createOrder(orderRequest);
            logger.info("Order created successfully with ID: {}", createdOrder.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder); // Return the created order
        } catch (Exception e) {
            logger.error("Error creating order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        try {
            logger.info("Received request to get order by ID: {}", id);
            OrderResponse orderResponse = orderService.getOrderById(id);
            logger.info("Order retrieved successfully with ID: {}", id);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e) {
            logger.error("Error retrieving order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestOrderByUserIdAndCourseId(
            @RequestParam Long userId, @RequestParam Long courseId) {
        try {
            logger.info("Received request to get latest order for user ID: {}, course ID: {}", userId, courseId);
            Optional<OrderResponse> orderResponse = orderService.getLatestOrderByUserIdAndCourseId(userId, courseId);

            if (orderResponse.isPresent()) {
                logger.info("Latest order retrieved successfully for user ID: {}, course ID: {}", userId, courseId);
                return ResponseEntity.ok(orderResponse.get());
            } else {
                logger.warn("No order found for user ID: {}, course ID: {}", userId, courseId);
                return ResponseEntity.ok().body(null); // Trả về rỗng nếu không tìm thấy hóa đơn
            }
        } catch (Exception e) {
            logger.error("Error retrieving latest order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving the order");
        }
    }
    @GetMapping("/revenue")
    public List<RevenueResponse> getMonthlyRevenue(@RequestParam int year) {
        return orderService.getMonthlyRevenue(year);
    }

    @GetMapping("/total-revenue")
    public ResponseEntity<BigDecimal> getTotalRevenue() {
        return ResponseEntity.ok(orderService.getTotalRevenueByStatusCompleted());
    }

    @GetMapping("/most-purchased-courses")
    public ResponseEntity<List<Course>> getMostPurchasedCourses() {
        List<Course> courses = orderService.getMostPurchasedCourses();
        if (courses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(courses);
    }

}
