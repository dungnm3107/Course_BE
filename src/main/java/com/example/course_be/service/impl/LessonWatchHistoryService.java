package com.example.course_be.service.impl;

import com.example.course_be.entity.Course;
import com.example.course_be.entity.Lesson;
import com.example.course_be.entity.LessonWatchHistory;
import com.example.course_be.repository.LessonRepository;
import com.example.course_be.repository.LessonWatchHistoryRepository;
import com.example.course_be.request.lesson.WatchHistoryRequest;
import com.example.course_be.response.lesson.LessonResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LessonWatchHistoryService {

    private final LessonWatchHistoryRepository lessonWatchHistoryRepository;

    private final LessonRepository lessonRepository;

    public LessonWatchHistoryService(LessonWatchHistoryRepository lessonWatchHistoryRepository , LessonRepository lessonRepository) {
        this.lessonWatchHistoryRepository = lessonWatchHistoryRepository;
        this.lessonRepository = lessonRepository;
    }

    // Lưu lại thời gian xem của người dùng
    public void saveWatchHistory(WatchHistoryRequest watchHistoryRequest) {
        // Tìm bài học dựa trên lessonId
        Lesson lesson = lessonRepository.findById(watchHistoryRequest.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        // Kiểm tra lịch sử xem đã tồn tại chưa
        Optional<LessonWatchHistory> historyOpt = lessonWatchHistoryRepository.findByUserIdAndLessonId(watchHistoryRequest.getUserId(), watchHistoryRequest.getLessonId());

        LessonWatchHistory history;
        if (historyOpt.isPresent()) {
            history = historyOpt.get();
        } else {
            history = new LessonWatchHistory();
            history.setUserId(watchHistoryRequest.getUserId());
            history.setLesson(lesson);
        }
        history.setLastWatchedTime(watchHistoryRequest.getWatchedTime());
        history.setLastWatchedAt(new Date());

        lessonWatchHistoryRepository.save(history);
    }



    // Lấy thời gian xem gần nhất của người dùng đối với bài học
    public Long getLastWatchedTime(Long userId, Long lessonId) {
        return lessonWatchHistoryRepository.findByUserIdAndLessonId(userId, lessonId)
                .map(LessonWatchHistory::getLastWatchedTime)
                .orElse(0L); // Nếu chưa có lịch sử thì trả về 0
    }

    public List<Course> getCourseWatchedByUserId(Long userId) {
        return lessonWatchHistoryRepository.findCoursesByUserId(userId);
    }


    public List<LessonResponse> getTop8MostWatchedLessons() {
        Pageable pageable = PageRequest.of(0, 8);
        List<Lesson> top8Lessons = lessonWatchHistoryRepository.findTopMostWatchedFreeLessons(pageable);

        return top8Lessons.stream().map(lesson -> new LessonResponse(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getContent(),
                lesson.getVideoUrl(),
                lesson.getLessonSequence(),
                lesson.getDeleted(),
                lesson.getChapter()
        )).collect(Collectors.toList());
    }
}
