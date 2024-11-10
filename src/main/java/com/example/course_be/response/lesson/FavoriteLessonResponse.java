package com.example.course_be.response.lesson;

import com.example.course_be.entity.Lesson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteLessonResponse {
    private Long id;
    private Long userId;
    private Lesson lesson;
    private Boolean deleted;
    private String createdAt;
}
