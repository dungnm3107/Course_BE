package com.example.course_be.request.lesson;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserLessonProgressRequest {
    private Long userId;
    private Long lessonId;
}
