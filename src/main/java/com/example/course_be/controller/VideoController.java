package com.example.course_be.controller;

import com.example.course_be.service.impl.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/video")
public class VideoController {

    private final S3Service s3Service;

    @Autowired
    public VideoController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadLargeVideo(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = s3Service.uploadLargeFile(file);
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    // generate presigned url
    @GetMapping("/get-presigned-url")
    public ResponseEntity<Map<String, String>> getPresignedUrl(@RequestParam("fileUrl") String fileUrl) {
        String presignedUrl = s3Service.getPresignedUrl(fileUrl);
        Map<String, String> response = new HashMap<>();
        response.put("url", presignedUrl);
        return ResponseEntity.ok(response);
    }


}
