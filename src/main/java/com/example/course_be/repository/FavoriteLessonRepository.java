package com.example.course_be.repository;

import com.example.course_be.entity.FavoriteLesson;
import com.example.course_be.entity.Lesson;
import com.example.course_be.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteLessonRepository extends JpaRepository<FavoriteLesson, Long> {

    Optional<FavoriteLesson> findByUserAndLesson(User user, Lesson lesson);
    List<FavoriteLesson> findByUserAndDeletedFalse(User user);

    @Query("SELECT f.lesson FROM FavoriteLesson f WHERE f.deleted = false GROUP BY f.lesson ORDER BY COUNT(f.lesson) DESC")
    List<Lesson> findMostFavoritedLessons(Pageable pageable);
}

