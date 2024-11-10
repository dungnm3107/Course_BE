package com.example.course_be.repository;


import com.example.course_be.entity.Chapter;
import com.example.course_be.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    boolean existsByTitle(String title);
    List<Lesson> findAllByChapterIdAndDeletedFalse(Long Id);
    List<Lesson> findByDeletedFalse();

}
