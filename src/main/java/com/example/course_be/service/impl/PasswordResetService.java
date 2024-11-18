package com.example.course_be.service.impl;

import com.example.course_be.entity.PasswordResetToken;
import com.example.course_be.entity.User;
import com.example.course_be.exceptions.AppException;
import com.example.course_be.exceptions.ErrorCode;
import com.example.course_be.repository.PasswordResetTokenRepository;
import com.example.course_be.repository.UserRepository;
import com.example.course_be.request.user.ChangePasswordRequest;
import com.example.course_be.request.user.TokenForgotPWRequest;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;


@Service
public class PasswordResetService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${reset.token.expiration.minutes:5}")
    private int tokenExpirationMinutes;
    @Transactional
    public String generateResetToken(String email) throws MessagingException {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new AppException(ErrorCode.EMAIL_NOT_FOUND);
        }
        // xoa ban ghi cu neu ton tai
        passwordResetTokenRepository.deleteByEmail(email);

        // tao ma xac thuc 6 chu so

        SecureRandom random = new SecureRandom();
        String token = String.format("%06d", random.nextInt(1000000));

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(email);
        resetToken.setExpiryDate(new Date(System.currentTimeMillis() + tokenExpirationMinutes * 60 * 1000));// 5p

        passwordResetTokenRepository.save(resetToken);
        //token in đậm và in hoa
        // Gửi email với HTML (token in đậm và in hoa)
        String emailContent = "<html><body>" +
                "<p><strong><font size=\"5\">" + token.toUpperCase() + "</font></strong></p>" +
                "<p>Đây là mã xác nhận của bạn để đặt lại mật khẩu. Vui lòng nhập mã này để tiếp tục.</p>" +
                "</body></html>";

        emailService.sendEmail(email, "Mã xác nhận đặt lại mật khẩu Course IT", emailContent);

        return token;

    }

    // kiem tra xem token co hop le hay khong
    public boolean validateResetToken(TokenForgotPWRequest tokenForgotPWRequest) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(tokenForgotPWRequest.getToken());
        if (passwordResetToken == null) {
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }

        // Kiểm tra token hết hạn hay chưa
        Date now = new Date();
        if (now.after(passwordResetToken.getExpiryDate())) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
        return true;
    }


    public void updatePassword(ChangePasswordRequest changePasswordRequest) {
        User user= userRepository.findByEmail(changePasswordRequest.getEmail());
        if(user == null) {
            throw new AppException(ErrorCode.EMAIL_NOT_FOUND);
        }
        String encodedPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
}
