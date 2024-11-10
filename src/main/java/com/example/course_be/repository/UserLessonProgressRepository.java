package com.example.course_be.repository;

import com.example.course_be.entity.UserLessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLessonProgressRepository extends JpaRepository<UserLessonProgress, Long> {
    List<UserLessonProgress> findByUserIdAndCompletedTrue(Long userId);
    UserLessonProgress findByUserIdAndLessonId(Long userId, Long lessonId);
}