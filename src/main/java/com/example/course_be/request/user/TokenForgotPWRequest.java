package com.example.course_be.request.user;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenForgotPWRequest {
    String token;
}
