package com.example.course_be.repository;

import com.example.course_be.entity.Course;
import com.example.course_be.entity.Order;
import com.example.course_be.enums.OrderStatus;
import com.example.course_be.response.Order.OrderResponse;
import com.example.course_be.response.Order.RevenueResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.course.id = :courseId ORDER BY o.createdDate DESC")
    List<Order> findLatestOrderByUserIdAndCourseId(Long userId, Long courseId, Pageable pageable);

    @Query("SELECT new com.example.course_be.response.Order.RevenueResponse(MONTH(o.createdDate), SUM(o.totalPrice)) " +
            "FROM Order o " +
            "WHERE YEAR(o.createdDate) = :year AND o.status = 'COMPLETED' " +
            "GROUP BY MONTH(o.createdDate)")
    List<RevenueResponse> findMonthlyRevenueByYear(@Param("year") int year);

    // Truy vấn để tìm số lần mua lớn nhất
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = com.example.course_be.enums.OrderStatus.COMPLETED " +
            "GROUP BY o.course ORDER BY COUNT(o) DESC LIMIT 1")
    Long findMaxPurchaseCount();

    // Truy vấn để lấy tất cả các khóa học có số lần mua bằng với số lần mua lớn nhất
    @Query("SELECT o.course FROM Order o WHERE o.status = com.example.course_be.enums.OrderStatus.COMPLETED " +
            "GROUP BY o.course HAVING COUNT(o) = ?1")
    List<Course> findCoursesWithMaxPurchaseCount(Long maxCount);

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = 'COMPLETED'")
    BigDecimal findTotalRevenueByStatusCompleted();
    // check xem khóa học đã được user  mua trước đó hay chưa
    boolean existsByCourseIdAndStatus(Long courseId, OrderStatus status);

    List<Order> findByUserId(Long userId);
}
