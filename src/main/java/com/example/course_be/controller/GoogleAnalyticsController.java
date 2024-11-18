package com.example.course_be.controller;


import com.example.course_be.service.impl.GoogleAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/google-analytics")
public class GoogleAnalyticsController {

    private final GoogleAnalyticsService googleAnalyticsService;

    @Autowired
    public GoogleAnalyticsController(GoogleAnalyticsService googleAnalyticsService) {
        this.googleAnalyticsService = googleAnalyticsService;
    }
// seccion(1 lượt truy cập) bắt đầu khi user truy cập vào trang web và kết thúc khi user không có hoạt động nào trong 30 phút.
    @GetMapping("/user-access")
    public ResponseEntity<Long> getActiveUsers() {
        try {
            long activeUsers = googleAnalyticsService.getActiveUsers();
            return ResponseEntity.ok(activeUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0L);
        }
    }
}


