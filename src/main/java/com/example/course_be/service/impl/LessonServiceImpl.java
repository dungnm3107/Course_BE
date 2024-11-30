package com.example.course_be.service.impl;

import com.example.course_be.entity.Chapter;
import com.example.course_be.entity.Lesson;
import com.example.course_be.entity.User;
import com.example.course_be.exceptions.AppException;
import com.example.course_be.exceptions.ErrorCode;
import com.example.course_be.repository.ChapterRepository;
import com.example.course_be.repository.LessonRepository;
import com.example.course_be.repository.UserRepository;
import com.example.course_be.request.lesson.LessonRequest;
import com.example.course_be.request.lesson.LessonUpdateRequest;
import com.example.course_be.response.lesson.LessonResponse;
import com.example.course_be.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final ChapterRepository chapterRepository;
    private final UserRepository userRepository;

    @Override
    public String saveLesson(LessonRequest lessonRequest) {
        try {
            boolean exitsLessonTitle = lessonRepository.existsByTitle(lessonRequest.getTitle());

            if (!exitsLessonTitle) {
                Optional<Chapter> chapter = Optional.ofNullable(chapterRepository.findById(lessonRequest.getIdChapter()).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND)));

                Lesson lesson = getLesson(lessonRequest, chapter);
                lessonRepository.save(lesson);
                return "Lesson saved successfully";
            } else {
                return "Lesson already exists";
            }
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED);
        }
    }

    @Override
    public String updateLesson(LessonUpdateRequest lessonUpdateRequest) {
        try {
            Optional<Lesson> lessonOptional = lessonRepository.findById(lessonUpdateRequest.getIdLesson());
            if (lessonOptional.isPresent()) {
                Lesson lesson = lessonOptional.get();
                Optional<Chapter> chapterOptional = chapterRepository.findById(lessonUpdateRequest.getIdChapter());
                if (chapterOptional.isPresent()) {
                    if (lessonUpdateRequest.getContent() != null) {
                        lesson.setContent(lessonUpdateRequest.getContent());
                    }
                    if (lessonUpdateRequest.getLessonSequence() != null) {
                        lesson.setLessonSequence(lessonUpdateRequest.getLessonSequence());
                    }
                    if (lessonUpdateRequest.getTitle() != null) {
                        lesson.setTitle(lessonUpdateRequest.getTitle());
                    }
                    if (lessonUpdateRequest.getVideoUrl() != null) {
                        lesson.setVideoUrl(lessonUpdateRequest.getVideoUrl());
                    }
                    if (lessonUpdateRequest.getIdChapter() != null) {
                        lesson.setChapter(chapterOptional.get());
                    }
                    lessonRepository.save(lesson);
                    return "Lesson updated successfully";
                }
            }
            return "Lesson updated failed";
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED);
        }
    }

    @Override
    public LessonResponse getLessonByID(Long id) {
        try {
            Optional<Lesson> lessonOptional = lessonRepository.findById(id);
            if (lessonOptional.isPresent()) {
                LessonResponse lessonResponse = new LessonResponse();
                lessonResponse.setIdLesson(lessonOptional.get().getId());
                lessonResponse.setTitle(lessonOptional.get().getTitle());
                lessonResponse.setContent(lessonOptional.get().getContent());
                lessonResponse.setDeleted(lessonOptional.get().getDeleted());
                lessonResponse.setVideoUrl(lessonOptional.get().getVideoUrl());
                lessonResponse.setLessonSequence(lessonOptional.get().getLessonSequence());
                return lessonResponse;
            }
            return null;
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED);
        }

    }


    public List<LessonResponse> getAllLesson(){

        List<Lesson> lessonList = lessonRepository.findByDeletedFalse();
        if(lessonList.isEmpty()){
            throw new AppException(ErrorCode.LESSON_NOT_FOUND);
        }
        List<LessonResponse> lessonResponseList = new ArrayList<>();
        for(Lesson lesson : lessonList){
            lessonResponseList.add(convertLessonToResponseGetAll(lesson));
        }
        return lessonResponseList;
    }
    @Override
    public List<LessonResponse> getLessonByChapterId(Long id) {
        if (id == null) {
            throw new AppException(ErrorCode.LESSON_NOT_FOUND);
        }
        List<Lesson> lessons = lessonRepository.findAllByChapterIdAndDeletedFalse(id);
//        if (lessons == null || lessons.isEmpty()) {
//            throw new AppException(ErrorCode.CHAPTER_NOT_FOUND); // hoặc xử lý hợp lý khác
//        }
        return lessons.stream()
                .map(this::convertLessonToResponseGetAll)
                .collect(Collectors.toList());
    }
    @Override
    public String deleteLessonById(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        lesson.setDeleted(true);
        lessonRepository.save(lesson);
        return "Course deleted successfully";
    }

    public LessonResponse convertLessonToResponseGetAll(Lesson lesson){
        LessonResponse lessonResponse = new LessonResponse();
        lessonResponse.setIdLesson(lesson.getId());
        lessonResponse.setTitle(lesson.getTitle());
        lessonResponse.setContent(lesson.getContent());
        lessonResponse.setVideoUrl(lesson.getVideoUrl());
        lessonResponse.setLessonSequence(lesson.getLessonSequence());
//        lessonResponse.setChapter(lesson.getChapter());
        return lessonResponse;
    }

    private Lesson getLesson(LessonRequest lessonRequest, Optional<Chapter> chapter) {
        if (chapter.isPresent()) {
            Lesson lesson = new Lesson();
            lesson.setLessonSequence(lessonRequest.getLessonSequence());
            lesson.setTitle(lessonRequest.getTitle());
            lesson.setChapter(chapter.get());
            lesson.setContent(lessonRequest.getContent());
            lesson.setVideoUrl(lessonRequest.getVideoUrl());
            lesson.setDeleted(false);
            return lesson;
        } else {
            throw new AppException(ErrorCode.UNCATEGORIZED);
        }

    }
}
