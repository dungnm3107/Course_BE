package com.example.course_be.service.impl;

import com.example.course_be.config.GoogleCloudStorageConfig;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class GoogleCloudStorageService {

    private final Storage storage;
    private final String bucketName;

    @Autowired
    public GoogleCloudStorageService(Storage storage, GoogleCloudStorageConfig googleCloudStorageConfig) {
        this.storage = storage;
        this.bucketName = googleCloudStorageConfig.getBucketName();
    }

// Phương thức upload video lên GCS và tạo HLS segments
public String uploadVideo(MultipartFile file) throws IOException {
    String originalFileName = file.getOriginalFilename();
    String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

    // Tạo file tạm thời từ file upload
    Path tempFilePath = Files.createTempFile("upload-", originalFileName);
    try (InputStream inputStream = file.getInputStream()) {
        Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
    }

    // Đường dẫn chứa các tệp HLS (.m3u8, .ts)
    String hlsOutputDirectory = "/tmp/hls_output/" + uniqueFileName;
    File outputDir = new File(hlsOutputDirectory);
    if (!outputDir.exists()) {
        outputDir.mkdirs();
    }

    // Lệnh FFmpeg để chuyển đổi sang HLS
    String ffmpegCommand = String.format(
            "ffmpeg -i %s -preset fast -g 50 -sc_threshold 0 " +
                    "-c:v libx264 -c:a aac -strict experimental " +
                    "-f hls -hls_time 10 -hls_playlist_type vod " +
                    "-hls_segment_filename %s/segment_%%03d.ts %s/playlist.m3u8",
            tempFilePath.toAbsolutePath(),
            hlsOutputDirectory,
            hlsOutputDirectory
    );

    // Thực thi lệnh FFmpeg
    Process process = Runtime.getRuntime().exec(ffmpegCommand);
    try {
        process.waitFor();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    // Tải lên Google Cloud Storage
    File[] files = outputDir.listFiles();
    for (File fileInDir : files) {
        String cloudFileName = "hls/" + uniqueFileName + "/" + fileInDir.getName();
        try (InputStream inputStream = Files.newInputStream(fileInDir.toPath())) {
            String contentType = fileInDir.getName().endsWith(".m3u8") ? "application/vnd.apple.mpegurl" : "video/mp2t";
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, cloudFileName)
                    .setContentType(contentType)
                    .build();
            storage.create(blobInfo, inputStream);
            // Đặt quyền công khai cho tệp
            storage.update(blobInfo.toBuilder()
                    .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
                    .build());
        }
    }

    return uniqueFileName;
}


    // Tạo Signed URL cho tệp M3U8 Playlist
    public String generateHlsSignedUrl(String fileName) {
        String objectPath = "hls/" + fileName + "/playlist.m3u8";  // Đảm bảo tên tệp đúng với đường dẫn lưu trữ trên GCS
        BlobId blobId = BlobId.of(bucketName, objectPath);

        BlobInfo blobInfo = storage.get(blobId);
        if (blobInfo == null) {
            throw new RuntimeException("File không tồn tại trên Google Cloud Storage");
        }


        return storage.signUrl(
                blobInfo,
                50,
                TimeUnit.MINUTES,
                Storage.SignUrlOption.withV4Signature()
        ).toString();
    }
}
