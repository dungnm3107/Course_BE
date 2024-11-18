package com.example.course_be.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class GoogleCloudStorageConfig {
    @Getter
    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    // Lấy bucket name từ cấu hình
    @Getter
    @Value("${spring.cloud.gcp.storage.bucket-name}")
    private String bucketName;

    // Tạo đối tượng Storage để giao tiếp với Google Cloud Storage
    @Bean
    public Storage storage() throws IOException {
        // Đọc tệp JSON từ classpath
        ClassPathResource resource = new ClassPathResource("service-account.json");
        return StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                .build()
                .getService();
    }

}
