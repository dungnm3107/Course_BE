package com.example.course_be.controller;

import com.example.course_be.response.lesson.UserLessonProgressResponse;
import com.example.course_be.request.lesson.UserLessonProgressRequest;
import com.example.course_be.service.UserLessonProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/progress")
public class UserLessonProgressController {

    @Autowired
    private UserLessonProgressService service;

    @PostMapping()
    public ResponseEntity<UserLessonProgressResponse> saveProgress(@RequestBody UserLessonProgressRequest request) {
        UserLessonProgressResponse savedProgress = service.saveProgress(request);
        return ResponseEntity.ok(savedProgress);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<UserLessonProgressResponse>> getProgressByUserId(@RequestParam Long userId) {
        List<UserLessonProgressResponse> progressList = service.getProgressByUserId(userId);
        return ResponseEntity.ok(progressList);
    }
}
