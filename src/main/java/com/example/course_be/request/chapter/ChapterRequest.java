package com.example.course_be.request.chapter;

import com.example.course_be.infrastructure.constant.EntityProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterRequest {
    @NotNull(message = "Course ID is required")
    private Long courseId;
    @NotNull(message = "Title is not empty")
    @Size(min = EntityProperties.MAX_LENGTH_1, max = EntityProperties.MAX_LENGTH_100, message = "Title must be between 5 and 100 characters")
    @NotNull private String title;
    private String description;
    private Integer chapterSequence;
    private Boolean deleted;
}
