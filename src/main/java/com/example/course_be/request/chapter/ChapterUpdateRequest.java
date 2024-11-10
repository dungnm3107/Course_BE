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
public class ChapterUpdateRequest {
    private Long idChapter;
    @NotNull(message = "Title is not empty")
    @Size(min = EntityProperties.MAX_LENGTH_5, max = EntityProperties.MAX_LENGTH_100, message = "Title must be between 5 and 100 characters")
    @NotNull private String title;
    private String description;
    @NotNull(message = "Chapter sequence is required")
    private Integer chapterSequence;
    private Boolean deleted;
}
