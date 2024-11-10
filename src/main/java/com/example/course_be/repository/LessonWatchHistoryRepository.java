package com.example.course_be.repository;

import com.example.course_be.entity.Course;
import com.example.course_be.entity.Lesson;
import com.example.course_be.entity.LessonWatchHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LessonWatchHistoryRepository extends JpaRepository<LessonWatchHistory, Long> {

    // Tìm lịch sử xem video của người dùng theo bài học
    Optional<LessonWatchHistory> findByUserIdAndLessonId(Long userId, Long lessonId);

    @Query("SELECT DISTINCT lwh.lesson.chapter.course FROM LessonWatchHistory lwh WHERE lwh.userId = :userId")
    List<Course> findCoursesByUserId(@Param("userId") Long userId);
    // 8 bai hoc ( của hóa hoc free) xem nhieu nhat
    @Query("SELECT l.lesson FROM LessonWatchHistory l " +
            "JOIN l.lesson les " +
            "JOIN les.chapter ch " +
            "JOIN ch.course c " +
            "WHERE c.courseType = com.example.course_be.enums.CourseType.FREE " +
            "GROUP BY l.lesson " +
            "ORDER BY COUNT(l.lesson.id) DESC")
    List<Lesson> findTopMostWatchedFreeLessons(Pageable pageable);


}
