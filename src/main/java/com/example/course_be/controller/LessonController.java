package com.example.course_be.controller;

import com.example.course_be.exceptions.AppException;
import com.example.course_be.request.lesson.LessonRequest;
import com.example.course_be.request.lesson.LessonUpdateRequest;
import com.example.course_be.response.chapter.ChapterResponse;
import com.example.course_be.response.error.ApiResponse;
import com.example.course_be.response.lesson.LessonResponse;
import com.example.course_be.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/lesson")
@Slf4j
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @PostMapping("/save")
    public ResponseEntity<?> saveLesson(@Valid @RequestBody LessonRequest lessonRequest) {
        try {
            String isSaved = lessonService.saveLesson(lessonRequest);
            return getResponseEntity(isSaved);
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateLesson(@Valid @RequestBody LessonUpdateRequest lessonUpdateRequest) {
        try {
            String isSaved = lessonService.updateLesson(lessonUpdateRequest);
            return getResponseEntity(isSaved);
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<?> getAllLesson() {
        try {
            List<LessonResponse> listLessonResponses = lessonService.getAllLesson();
            ApiResponse<List<LessonResponse>> apiResponse = new ApiResponse<>();
            apiResponse.setCode(200);
            apiResponse.setResult(listLessonResponses);
            return ResponseEntity.ok(apiResponse);
        } catch (AppException e) {
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setCode(e.getErrorCode().getCode());
            errorResponse.setMessage(e.getErrorCode().getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLessonByID(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(lessonService.getLessonByID(id));
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getLessonByChapterId(@PathVariable Long id) {
        try {
            if (id != null) {
                ApiResponse<List<LessonResponse>> apiResponse = new ApiResponse<>();
                List<LessonResponse> lessonResponses = lessonService.getLessonByChapterId(id);
                apiResponse.setCode(200);
                apiResponse.setResult(lessonResponses);
                return ResponseEntity.ok(apiResponse);
            }
            return ResponseEntity.badRequest().body("Chapter not found");
        } catch (AppException e) {
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setCode(e.getErrorCode().getCode());
            errorResponse.setMessage(e.getErrorCode().getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{lessonId}")
    public ResponseEntity<String> deleteLessonById(@PathVariable Long lessonId) {
        String isDeleted = lessonService.deleteLessonById(lessonId);
        return ResponseEntity.ok(isDeleted);
    }

    static ResponseEntity<?> getResponseEntity(String isSaved) {
        if (isSaved != null) {
            ApiResponse<String> apiResponse = new ApiResponse<>();
            apiResponse.setCode(201);
            apiResponse.setMessage(isSaved);
            return ResponseEntity.ok(apiResponse);
        }
        return ResponseEntity.badRequest().body(isSaved);
    }

    private ApiResponse<String> buildErrorResponse(AppException e) {
        ApiResponse<String> errorResponse = new ApiResponse<>();
        errorResponse.setCode(e.getErrorCode().getCode());
        errorResponse.setMessage(e.getMessage());
        return errorResponse;
    }

}
