package com.example.course_be.request.lesson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteLessonRequest {
    private Long userId;
    private Long lessonId;
}