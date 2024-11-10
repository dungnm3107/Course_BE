package com.example.course_be.service.impl;

import com.example.course_be.entity.Lesson;
import com.example.course_be.entity.User;
import com.example.course_be.entity.UserLessonProgress;
import com.example.course_be.repository.LessonRepository;
import com.example.course_be.repository.UserLessonProgressRepository;
import com.example.course_be.repository.UserRepository;
import com.example.course_be.request.lesson.UserLessonProgressRequest;
import com.example.course_be.response.lesson.UserLessonProgressResponse;
import com.example.course_be.service.UserLessonProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserLessonProgressServiceImpl implements UserLessonProgressService {

    @Autowired
    private UserLessonProgressRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Override
    public UserLessonProgressResponse saveProgress(UserLessonProgressRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        // Kiểm tra xem tiến trình đã tồn tại chưa
        UserLessonProgress existingProgress = repository.findByUserIdAndLessonId(user.getId(), lesson.getId());
        if (existingProgress != null) {
            return mapToResponse(existingProgress);
        }

        // Nếu chưa tồn tại, tạo mới
        UserLessonProgress progress = new UserLessonProgress();
        progress.setUser(user);
        progress.setLesson(lesson);
        progress.setCompleted(true);
        progress.setCompletedAt(new Date());

        UserLessonProgress savedProgress = repository.save(progress);

        return mapToResponse(savedProgress);
    }

    @Override
    public List<UserLessonProgressResponse> getProgressByUserId(Long userId) {
        return repository.findByUserIdAndCompletedTrue(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private UserLessonProgressResponse mapToResponse(UserLessonProgress progress) {
        UserLessonProgressResponse response = new UserLessonProgressResponse();
        response.setId(progress.getId());
        response.setUserId(progress.getUser().getId());
        response.setLessonId(progress.getLesson().getId());
        response.setCompleted(progress.getCompleted());
        response.setCompletedAt(progress.getCompletedAt());
        return response;
    }
}
