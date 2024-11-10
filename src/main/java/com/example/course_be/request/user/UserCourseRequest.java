package com.example.course_be.request.user;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCourseRequest {
    private Long userId;
    private Long courseId;
}
