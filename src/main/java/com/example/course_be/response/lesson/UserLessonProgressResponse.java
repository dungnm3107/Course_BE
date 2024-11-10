package com.example.course_be.response.lesson;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserLessonProgressResponse {
    private Long id;
    private Long userId;
    private Long lessonId;
    private Boolean completed;
    private Date completedAt;
}
