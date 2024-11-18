package com.example.course_be.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.MailException;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            // Sử dụng MimeMessageHelper để gửi email
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true: gửi với định dạng HTML
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);  // true để chỉ định rằng nội dung là HTML


            mailSender.send(message);

        } catch (MailException e) {
            e.printStackTrace();
        }
    }
}
