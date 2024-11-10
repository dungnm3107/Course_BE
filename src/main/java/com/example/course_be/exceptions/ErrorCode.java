package com.example.course_be.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@NoArgsConstructor

public enum ErrorCode {
    UNCATEGORIZED(500, "Lỗi không xác định ", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_FOUND(404, "Người dùng không tồn tại ", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS(409, "Người dùng đã tồn tại ", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS(409, "Email đã tồn tại ", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS(401, "Thông tin xác thực không hợp lệ", HttpStatus.UNAUTHORIZED),
    INVALID_REQUEST(400, "Yêu cầu không hợp lệ", HttpStatus.BAD_REQUEST),
    PASS_USERNAME_INCORRECT(400, "Tên người dùng và mật khẩu không đúng ", HttpStatus.BAD_REQUEST),
    COURSE_NOT_FOUND(404, "Khóa học không tồn tại ", HttpStatus.NOT_FOUND),
    COURSE_ALREADY_EXISTS(409, "Khóa học đã tồn tại ", HttpStatus.CONFLICT),
    COURSE_SAVE_ERROR(500, "Lưu khóa học không thành công ", HttpStatus.INTERNAL_SERVER_ERROR),
    CHAPTER_NOT_FOUND(404, "Chapter not found", HttpStatus.NOT_FOUND),
    CHAPTER_ALREADY_EXISTS(409, "Chapter already exists", HttpStatus.CONFLICT),
    LESSON_NOT_FOUND(404, "Lesson not found", HttpStatus.NOT_FOUND),
    ;


    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
