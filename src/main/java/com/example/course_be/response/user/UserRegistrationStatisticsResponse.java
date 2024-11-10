package com.example.course_be.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationStatisticsResponse {
    private Integer month;
    private Long totalUsers;
}
