package com.example.course_be.controller;

import com.example.course_be.exceptions.AppException;
import com.example.course_be.request.chapter.ChapterRequest;
import com.example.course_be.request.chapter.ChapterUpdateRequest;
import com.example.course_be.response.chapter.ChapterResponse;
import com.example.course_be.response.course.CourseResponse;
import com.example.course_be.response.error.ApiResponse;
import com.example.course_be.service.ChapterService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/chapter")
@Slf4j
public class ChapterController {

    private ChapterService chapterService;

    @Autowired
    public void setChapterService(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveCourse(@Valid @RequestBody ChapterRequest chapterRequest) {
        try {
            String isSaved = chapterService.saveChapter(chapterRequest);
            return getResponseEntity(isSaved);
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping
    public ResponseEntity<?> getAllChapter() {
        try {
            List<ChapterResponse> listChapterResponses = chapterService.getAllChapter();
            ApiResponse<List<ChapterResponse>> apiResponse = new ApiResponse<>();
            apiResponse.setCode(200);
            apiResponse.setResult(listChapterResponses);
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
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getChapterByCourseId(@PathVariable Long id) {
        try {
            if (id != null) {
                ApiResponse<List<ChapterResponse>> apiResponse = new ApiResponse<>();
                List<ChapterResponse> chapterResponses = chapterService.getChapterByCourseId(id);
                apiResponse.setCode(200);
                apiResponse.setResult(chapterResponses);
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

    @PutMapping("/update")
    public ResponseEntity<?> updateChapter(@Valid @RequestBody ChapterUpdateRequest chapterUpdateRequest) {
        try {
            String isUpdated = chapterService.updateChapter(chapterUpdateRequest);
            return getResponseEntity(isUpdated);
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{chapterId}")
    public ResponseEntity<String> deleteChapterById(@PathVariable Long chapterId) {
        String isDeleted = chapterService.deleteChapterById(chapterId);
        return ResponseEntity.ok(isDeleted);
    }

    private ResponseEntity<?> getResponseEntity(String isUpdated) {
        if (isUpdated != null) {
            ApiResponse<String> apiResponse = new ApiResponse<>();
            apiResponse.setCode(200);
            apiResponse.setMessage(isUpdated);
            return ResponseEntity.ok(apiResponse);
        }
        return ResponseEntity.badRequest().body(isUpdated);
    }

    private ApiResponse<String> buildErrorResponse(AppException e) {
        ApiResponse<String> errorResponse = new ApiResponse<>();
        errorResponse.setCode(e.getErrorCode().getCode());
        errorResponse.setMessage(e.getMessage());
        return errorResponse;
    }
}
