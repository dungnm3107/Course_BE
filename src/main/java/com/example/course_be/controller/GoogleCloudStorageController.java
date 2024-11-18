package com.example.course_be.controller;
import com.example.course_be.service.impl.GoogleCloudStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@RestController
@RequestMapping("api/v1/video/gcs")
public class GoogleCloudStorageController {
    private final GoogleCloudStorageService googleCloudStorageService;

    @Autowired
    public GoogleCloudStorageController(GoogleCloudStorageService googleCloudStorageService) {
        this.googleCloudStorageService = googleCloudStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            // Upload video và tạo các tệp HLS (M3U8, TS)
            String fileUrl = googleCloudStorageService.uploadVideo(file);
            return ResponseEntity.ok(fileUrl);  // Trả về URL của video vừa upload lên GCS
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload failed");
        }
    }

    @GetMapping("/get-url")
    public ResponseEntity<String> getHlsUrl(@RequestParam("fileName") String fileName) {
        // Gọi API generateHlsSignedUrl với fileName nhận được từ client
        String signedUrl = googleCloudStorageService.generateHlsSignedUrl(fileName);

        // Trả về signed URL của tệp M3U8
        return ResponseEntity.ok(signedUrl);
    }


}
