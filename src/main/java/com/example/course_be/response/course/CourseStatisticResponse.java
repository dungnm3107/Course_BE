package com.example.course_be.response.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseStatisticResponse {
    private long totalCourses;
    private long totalChapters;
    private long totalLessons;
}
