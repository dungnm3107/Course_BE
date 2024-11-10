package com.example.course_be.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    private static final long PART_SIZE = 5 * 1024 * 1024; // Kích thước của mỗi part (5MB)

    public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    // Multipart upload method
    public String uploadLargeFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();

        // 1. Khởi tạo multipart upload
        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();
        CreateMultipartUploadResponse response = s3Client.createMultipartUpload(createMultipartUploadRequest);
        String uploadId = response.uploadId();

        List<CompletedPart> completedParts = new ArrayList<>();

        // 2. Tải lên từng phần
        try (InputStream inputStream = file.getInputStream()) {
            long fileLength = file.getSize();
            long partCount = (fileLength + PART_SIZE - 1) / PART_SIZE;

            for (int partNumber = 1; partNumber <= partCount; partNumber++) {
                byte[] buffer = new byte[(int) Math.min(PART_SIZE, fileLength - (partNumber - 1) * PART_SIZE)];
                inputStream.read(buffer);

                // Upload từng phần
                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .uploadId(uploadId)
                        .partNumber(partNumber)
                        .build();
                UploadPartResponse uploadPartResponse = s3Client.uploadPart(uploadPartRequest,
                        RequestBody.fromBytes(buffer));

                // Lưu lại thông tin của part
                completedParts.add(CompletedPart.builder()
                        .partNumber(partNumber)
                        .eTag(uploadPartResponse.eTag())
                        .build());
            }

            // 3. Hoàn thành upload
            CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .uploadId(uploadId)
                    .multipartUpload(CompletedMultipartUpload.builder().parts(completedParts).build())
                    .build();
            s3Client.completeMultipartUpload(completeMultipartUploadRequest);

            // Tạo URL gốc không có chữ ký
            String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);

            return fileUrl;

        } catch (Exception e) {
            // Hủy bỏ upload nếu có lỗi
            AbortMultipartUploadRequest abortMultipartUploadRequest = AbortMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .uploadId(uploadId)
                    .build();
            s3Client.abortMultipartUpload(abortMultipartUploadRequest);
            throw new IOException("Multipart upload failed", e);
        }
    }


    public String getPresignedUrl(String fileUrl) {
        String key = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))  // Thời hạn tùy chỉnh
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
        return presignedGetObjectRequest.url().toString();
    }

}
