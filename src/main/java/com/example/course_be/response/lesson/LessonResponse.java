package com.example.course_be.response.lesson;

import com.example.course_be.entity.Chapter;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LessonResponse {
    private Long idLesson;
    private String title;
    private String content;
    private String videoUrl;
    private Integer lessonSequence;
    private Boolean deleted;
    private Chapter chapter;
}
