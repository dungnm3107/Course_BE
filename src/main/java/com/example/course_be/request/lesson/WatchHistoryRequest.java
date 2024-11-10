package com.example.course_be.request.lesson;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatchHistoryRequest {
    private Long lessonId;
    private Long userId;
    private Long watchedTime;
}