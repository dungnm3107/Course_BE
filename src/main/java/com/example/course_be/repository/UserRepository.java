package com.example.course_be.repository;

import com.example.course_be.entity.User;
import com.example.course_be.response.user.UserRegistrationStatisticsResponse;
import com.example.course_be.response.user.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String username);

    User findByEmail(String email);

    boolean existsByUserName(String username);

    boolean existsByEmail(String email);


    @Query("SELECT new com.example.course_be.response.user.UserResponse(u.id, u.userName, u.fullName, u.email, u.avatar, u.gender, u.phone, u.isDeleted) " +
            "FROM User u WHERE u.isDeleted = false")
    List<UserResponse> getAllActiveUsers();

    long countByIsDeletedFalse();

    Optional<User> getUserById(Long userId);

    @Query("SELECT new com.example.course_be.response.user.UserRegistrationStatisticsResponse(FUNCTION('MONTH', u.createdDate), COUNT(u.id)) " +
            "FROM User u " +
            "WHERE u.isDeleted = false AND FUNCTION('YEAR', u.createdDate) = :year " +
            "GROUP BY FUNCTION('MONTH', u.createdDate)")
    List<UserRegistrationStatisticsResponse> countUserRegistrationsByMonthAndYear(@Param("year") int year);
}
