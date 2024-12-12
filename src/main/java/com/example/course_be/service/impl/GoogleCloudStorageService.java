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

    //  upload video lên GCS và tạo HLS segments
    public String uploadVideo(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        // tạo file tạm thời từ file upload
        Path tempFilePath = Files.createTempFile("upload-", originalFileName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);  // nếu tệp tồn tại thì ghi đè lên
        }

        // Đường dẫn chứa các tệp HLS (.m3u8, .ts) trên hệ thống
        String hlsOutputDirectory = "/tmp/hls_output/" + uniqueFileName;
        File outputDir = new File(hlsOutputDirectory);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // chuyển đổi sang HLS
        String ffmpegCommand = String.format(
                "ffmpeg -i %s -preset fast -g 50 -sc_threshold 0 " +
                        "-c:v libx264 -c:a aac -strict experimental " +
                        "-f hls -hls_time 10 -hls_playlist_type vod " +
                        "-hls_segment_filename %s/segment_%%03d.ts %s/playlist.m3u8",
                tempFilePath.toAbsolutePath(),
                hlsOutputDirectory,
                hlsOutputDirectory
        );
        
        //  -i %s: Đường dẫn tệp video đầu vào (thay bằng tempFilePath.toAbsolutePath()).
        //  -preset fast: Xác định tốc độ nén video(fast cân bằng giữa tốc độ và chất lượng, ultrafast tăng tốc độ nhưng giản chất lượng)
        //  -g 50:(Gói 50 khung hình vào 1 Group of Pictures (GOP))
        //  -sc_threshold 0: Ngăn FFmpeg tự động tạo điểm cắt ở giữa các đoạn video.
        //  -c:v libx264: Chọn codec H.264 để mã hóa video.
        //  -c:a aac: Chọn codec AAC để mã hóa âm thanh.
        //  -strict experimental: Cho phép các tính năng chưa hoàn thiện trong FFmpeg (áp dụng với AAC trong một số trường hợp).
        //  -f hls: Chỉ định định dạng đầu ra là HLS
        //  -hls_time 10: mỗi segment .ts có time 10s
        //  -hls_playlist_type vod: Xác định đây là một HLS VOD (Video on Demand) playlist.
        //  -hls_segment_filename %s/segment_%%03d.ts: Định dạng tên của từng tệp đoạn (.ts)
        //  %s: Thư mục đích (thay bằng hlsOutputDirectory).
        //  %%03d: Đánh số các đoạn, ví dụ: segment_001.ts, segment_002.ts.
        //  %s/playlist.m3u8: Tạo một tệp playlist .m3u8 chứa danh sách các đoạn .ts.

        Process process = Runtime.getRuntime().exec(ffmpegCommand);
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // phản hồi mua khoa học

       // tải lên GCS ( áp dụng tải song song)
        File[] files = outputDir.listFiles();
        Arrays.stream(files).parallel().forEach(fileInDir -> { // java sử dụng ForkJoinPool giúp tự động điều chỉnh luồng dể tối ưu tải
            String cloudFileName = "hls/" + uniqueFileName + "/" + fileInDir.getName();
            try (InputStream inputStream = Files.newInputStream(fileInDir.toPath())) {
                // Xác định loại nội dung tệp
                String contentType = fileInDir.getName().endsWith(".m3u8") ? "application/vnd.apple.mpegurl" : "video/mp2t";
                BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, cloudFileName)
                        .setContentType(contentType)
                        .build();
                storage.create(blobInfo, inputStream);
                // Đặt quyền công khai cho tệp
                storage.update(blobInfo.toBuilder()
                        .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
                        .build());
            } catch (IOException e) {
                throw new RuntimeException("Error uploading file: " + fileInDir.getName(), e);
            }
        });

        return uniqueFileName;
    }


    // Tạo Signed URL cho tệp M3U8 Playlist
    public String generateHlsSignedUrl(String fileName) {
        String objectPath = "hls/" + fileName + "/playlist.m3u8";
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
