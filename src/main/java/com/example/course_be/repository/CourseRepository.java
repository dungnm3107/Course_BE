package com.example.course_be.repository;

import com.example.course_be.entity.Course;
import com.example.course_be.enums.CourseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, CourseRepositoryCustom {
     boolean existsByTitle(String courseName);
     boolean existsById(long courseId);
     List<Course> findByCourseTypeAndDeletedFalse(CourseType courseType);
     List<Course> findByDeletedFalse();

     @Query("SELECT COUNT(c) FROM Course c WHERE c.deleted = false")
     long countCourses();

     @Query("SELECT COUNT(ch) FROM Chapter ch WHERE ch.deleted = false")
     long countChapters();

     @Query("SELECT COUNT(l) FROM Lesson l WHERE l.deleted = false")
     long countLessons();
}
