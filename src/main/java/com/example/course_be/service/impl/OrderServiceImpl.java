package com.example.course_be.service.impl;


import com.example.course_be.entity.Course;
import com.example.course_be.entity.Order;
import com.example.course_be.entity.User;
import com.example.course_be.enums.OrderStatus;
import com.example.course_be.repository.CourseRepository;
import com.example.course_be.repository.OrderRepository;
import com.example.course_be.repository.UserRepository;
import com.example.course_be.request.order.OrderRequest;
import com.example.course_be.response.Order.OrderResponse;
import com.example.course_be.response.Order.RevenueResponse;
import com.example.course_be.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    public Order createOrder(OrderRequest orderRequest) {
        logger.info("Creating order for user ID: {}, course ID: {}", orderRequest.getUserId(), orderRequest.getCourseId());

        // Fetch User
        User user = userRepository.findById(orderRequest.getUserId())
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", orderRequest.getUserId());
                    return new RuntimeException("User not found");
                });

        // Fetch Course
        Course course = courseRepository.findById(orderRequest.getCourseId())
                .orElseThrow(() -> {
                    logger.error("Course not found with ID: {}", orderRequest.getCourseId());
                    return new RuntimeException("Course not found");
                });

        // Create Order
        Order order = new Order();
        order.setUser(user);
        order.setCourse(course);
        order.setTotalPrice(orderRequest.getTotalPrice());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod("VNPAY");

        logger.info("Saving order to the database");
        Order savedOrder = orderRepository.save(order);
        logger.info("Order saved with ID: {}", savedOrder.getId());

        return savedOrder;
    }

    @Override
    public void updateOrderStatus(Long orderId, boolean isPaymentSuccess) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.error("Order not found with ID: {}", orderId);
                    return new RuntimeException("Order not found");
                });
        order.setStatus(isPaymentSuccess ? OrderStatus.COMPLETED : OrderStatus.FAILED);
        orderRepository.save(order);
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.error("Order not found with ID: {}", orderId);
                    return new RuntimeException("Order not found");
                });

        return convertOrderToResponse(order);
    }

    // lấy ra hóa đơn mới nhất theo userId và courseId
    @Override
    public Optional<OrderResponse> getLatestOrderByUserIdAndCourseId(Long userId, Long courseId) {
        Pageable pageable = PageRequest.of(0, 1); // Chỉ lấy 1 kết quả
        List<Order> orders = orderRepository.findLatestOrderByUserIdAndCourseId(userId, courseId, pageable);

        if (orders.isEmpty()) {
            return Optional.empty(); // Trả về Optional.empty nếu không tìm thấy hóa đơn
        }

        Order latestOrder = orders.get(0);
        return Optional.of(convertOrderToResponse(latestOrder)); // Trả về Optional chứa OrderResponse
    }

    @Override
    public List<RevenueResponse> getMonthlyRevenue(int year) {
        return orderRepository.findMonthlyRevenueByYear(year); // Sử dụng enum
    }
    // khóa học mua nhiều nhất
    @Override
    public List<Course> getMostPurchasedCourses() {
        Long maxPurchaseCount = orderRepository.findMaxPurchaseCount();
        if (maxPurchaseCount != null) {
            return orderRepository.findCoursesWithMaxPurchaseCount(maxPurchaseCount);
        }
        return List.of(); // Trả về danh sách rỗng nếu không có đơn hàng nào hoàn thành
    }
    // tong doanh thu
    @Override
    public BigDecimal getTotalRevenueByStatusCompleted() {
        BigDecimal totalRevenue = orderRepository.findTotalRevenueByStatusCompleted();
        return totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        // Tìm các đơn hàng của người dùng theo userId
        return orders.stream()
                .map(this::convertOrderToResponse)
                .collect(Collectors.toList());
    }


    private OrderResponse convertOrderToResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getCourse().getId(),
                order.getTotalPrice(),
                order.getStatus().name(),
                order.getPaymentMethod()
        );
    }


}
