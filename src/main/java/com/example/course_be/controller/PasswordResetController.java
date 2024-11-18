package com.example.course_be.controller;


import com.example.course_be.request.user.ChangePasswordRequest;
import com.example.course_be.request.user.TokenForgotPWRequest;
import com.example.course_be.service.impl.PasswordResetService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/password-reset")
public class PasswordResetController {
    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping
    public ResponseEntity<String> forgotPassword(@RequestParam String email) throws MessagingException {
        passwordResetService.generateResetToken(email);
        return ResponseEntity.ok("Mã xác nhận đã gửi thành công tới email: " + email);
    }

    @PostMapping("/validate-token")
    public ResponseEntity<String> validateToken(@RequestBody TokenForgotPWRequest tokenForgotPWRequest) {
        passwordResetService.validateResetToken(tokenForgotPWRequest);
        return ResponseEntity.ok("Xác nhận token thành công ");
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> updatePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        passwordResetService.updatePassword(changePasswordRequest);
        return ResponseEntity.ok("Đặt lại mật khẩu thành công ");
    }
}
