package com.example.course_be.service.impl;

import com.example.course_be.entity.Chapter;
import com.example.course_be.entity.Course;
import com.example.course_be.entity.Lesson;
import com.example.course_be.exceptions.AppException;
import com.example.course_be.exceptions.ErrorCode;
import com.example.course_be.repository.ChapterRepository;
import com.example.course_be.repository.CourseRepository;
import com.example.course_be.repository.LessonRepository;
import com.example.course_be.request.chapter.ChapterRequest;
import com.example.course_be.request.chapter.ChapterUpdateRequest;
import com.example.course_be.response.chapter.ChapterResponse;
import com.example.course_be.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;

    @Override
    public String saveChapter(ChapterRequest chapterRequest) {
        try {
            boolean exitsChapterTitle = chapterRepository.existsByTitle(chapterRequest.getTitle());

            if (!exitsChapterTitle) {
                chapterRepository.save(getChapter(chapterRequest));
                return "Chapter saved successfully";
            } else {
                return "Chapter already exists";
            }
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED);
        }
    }

    @Override
    public String updateChapter(ChapterUpdateRequest chapterUpdateRequest) {
        try {
            Optional<Chapter> chapterOptional = chapterRepository.findById(chapterUpdateRequest.getIdChapter());
            if (chapterOptional.isEmpty()) {
                throw new AppException(ErrorCode.CHAPTER_NOT_FOUND);
            }
            Chapter chapter = chapterOptional.get();
            chapter.setTitle(chapterUpdateRequest.getTitle());
            chapter.setChapterSequence(chapterUpdateRequest.getChapterSequence());
            chapter.setDescription(chapterUpdateRequest.getDescription());
            chapterRepository.save(chapter);
            return "Chapter updated successfully";
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED);
        }
    }

    @Override
    public List<ChapterResponse> getAllChapter() {
        List<Chapter> listChapter = chapterRepository.findByDeletedFalse();
        if (listChapter.isEmpty()) {
            throw new AppException(ErrorCode.CHAPTER_NOT_FOUND);
        }
        List<ChapterResponse> listChapterResponse = new ArrayList<>();
        for (Chapter chapter : listChapter) {
            listChapterResponse.add(convertChapterToResponse(chapter));
        }
        return listChapterResponse;
    }

    @Override
    public List<ChapterResponse> getChapterByCourseId(Long id) {
        if (id == null) {
            throw new AppException(ErrorCode.CHAPTER_NOT_FOUND);
        }
        List<Chapter> chapters = chapterRepository.findAllByCourseIdAndDeletedFalse(id);
        if (chapters == null || chapters.isEmpty()) {
            throw new AppException(ErrorCode.CHAPTER_NOT_FOUND); // hoặc xử lý hợp lý khác
        }
        // Chuyển đổi danh sách Chapter sang ChapterResponse
        return chapters.stream()
                .map(this::convertChapterToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public String deleteChapterById(Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
        chapter.setDeleted(true);
        chapterRepository.save(chapter);
        return "Course deleted successfully";
    }

    public ChapterResponse convertChapterToResponse(Chapter chapter) {
        ChapterResponse chapterResponse = new ChapterResponse();
        List<Lesson> listLesson = lessonRepository.findAllByChapterIdAndDeletedFalse(chapter.getId());
        chapterResponse.setId(chapter.getId());
        chapterResponse.setTitle(chapter.getTitle());
        chapterResponse.setDescription(chapter.getDescription());
        chapterResponse.setChapterSequence(chapter.getChapterSequence());
        chapterResponse.setLessons(listLesson);

        return chapterResponse;
    }


    private Chapter getChapter(ChapterRequest chapterRequest) {
        Optional<Course> course = Optional.ofNullable(courseRepository.findById(chapterRequest.getCourseId()).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND)));
        if (course.isEmpty()) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }
        Chapter chapter = new Chapter();
        chapter.setCourse(course.get());
        chapter.setChapterSequence(chapterRequest.getChapterSequence());
        chapter.setTitle(chapterRequest.getTitle());
        chapter.setDescription(chapterRequest.getDescription());
        chapter.setDeleted(false);
        return chapter;
    }
}
