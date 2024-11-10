package com.example.course_be.response.chapter;

import com.example.course_be.entity.Course;
import com.example.course_be.entity.Lesson;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChapterResponse {
    private Long id;
    private String title;
    private String description;
    private Integer chapterSequence;
    private Boolean deleted;
    private List<Lesson> lessons;
    private Course course;

}
