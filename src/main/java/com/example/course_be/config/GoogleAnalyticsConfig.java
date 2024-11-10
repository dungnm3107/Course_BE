package com.example.course_be.config;

import com.google.analytics.data.v1beta.BetaAnalyticsDataClient;
import com.google.analytics.data.v1beta.BetaAnalyticsDataSettings;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

@Configuration
public class GoogleAnalyticsConfig {

    @Bean
    public BetaAnalyticsDataClient betaAnalyticsDataClient() throws IOException {
        ClassPathResource resource = new ClassPathResource("service-account.json");

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(resource.getInputStream())
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/analytics.readonly"));

        BetaAnalyticsDataSettings settings = BetaAnalyticsDataSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .build();

        return BetaAnalyticsDataClient.create(settings);
    }
}

