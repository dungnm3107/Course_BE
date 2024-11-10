package com.example.course_be.service;

import com.example.course_be.request.chapter.ChapterRequest;
import com.example.course_be.request.chapter.ChapterUpdateRequest;
import com.example.course_be.response.chapter.ChapterResponse;
import com.example.course_be.response.course.CourseResponse;

import java.util.List;

public interface ChapterService {

    String saveChapter(ChapterRequest chapterRequest);

    String updateChapter(ChapterUpdateRequest chapterUpdateRequest);

    List<ChapterResponse> getAllChapter();

    List<ChapterResponse> getChapterByCourseId(Long id);

    String deleteChapterById(Long chapterId);
}
