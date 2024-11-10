package com.example.course_be.service.impl;

import com.example.course_be.entity.FavoriteLesson;
import com.example.course_be.entity.Lesson;
import com.example.course_be.entity.User;
import com.example.course_be.repository.FavoriteLessonRepository;
import com.example.course_be.repository.LessonRepository;
import com.example.course_be.repository.UserRepository;
import com.example.course_be.request.lesson.FavoriteLessonRequest;
import com.example.course_be.response.lesson.FavoriteLessonResponse;
import com.example.course_be.service.FavoriteLessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteLessonServiceImpl implements FavoriteLessonService {

    @Autowired
    private FavoriteLessonRepository favoriteLessonRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Override
    public FavoriteLessonResponse createOrUpdateFavoriteLesson(FavoriteLessonRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        // Check if the favorite lesson already exists
        Optional<FavoriteLesson> existingFavoriteLesson = favoriteLessonRepository.findByUserAndLesson(user, lesson);
        FavoriteLesson favoriteLesson;

        if (existingFavoriteLesson.isPresent()) {
            favoriteLesson = existingFavoriteLesson.get();
            // Toggle the deleted status
            favoriteLesson.setDeleted(!favoriteLesson.getDeleted());
        } else {
            favoriteLesson = new FavoriteLesson();
            favoriteLesson.setUser(user);
            favoriteLesson.setLesson(lesson);
            favoriteLesson.setDeleted(false); // Initially set to not deleted
        }

        FavoriteLesson savedFavoriteLesson = favoriteLessonRepository.save(favoriteLesson);
        return mapToFavoriteLessonResponse(savedFavoriteLesson);
    }


    @Override
    public List<FavoriteLessonResponse> getFavoriteLessonsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<FavoriteLesson> favoriteLessons = favoriteLessonRepository.findByUserAndDeletedFalse(user);

        return favoriteLessons.stream()
                .map(this::mapToFavoriteLessonResponse)
                .collect(Collectors.toList());
    }

    public List<Lesson> getTop4MostFavoritedLessons() {
        // Sử dụng PageRequest để giới hạn số lượng kết quả trả về
        return favoriteLessonRepository.findMostFavoritedLessons(PageRequest.of(0, 4));
    }

    // Helper method to map FavoriteLesson to FavoriteLessonResponse
    private FavoriteLessonResponse mapToFavoriteLessonResponse(FavoriteLesson favoriteLesson) {
        FavoriteLessonResponse response = new FavoriteLessonResponse();
        response.setId(favoriteLesson.getId());
        response.setUserId(favoriteLesson.getUser().getId());
        response.setLesson(favoriteLesson.getLesson());
        response.setDeleted(favoriteLesson.getDeleted());
        response.setCreatedAt(favoriteLesson.getCreatedAt().toString());
        return response;
    }

}

