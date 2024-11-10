package com.example.course_be.service.impl;

import com.google.analytics.data.v1beta.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GoogleAnalyticsService {

    private final BetaAnalyticsDataClient betaAnalyticsDataClient;

    @Value("${google.analytics.property-id}")
    private String propertyId;

    public GoogleAnalyticsService(BetaAnalyticsDataClient betaAnalyticsDataClient) {
        this.betaAnalyticsDataClient = betaAnalyticsDataClient;
    }

    public long getActiveUsers() throws IOException {
        RunReportRequest request = RunReportRequest.newBuilder()
                .setProperty("properties/" + propertyId)
                .addDimensions(Dimension.newBuilder().setName("date").build())
                .addMetrics(Metric.newBuilder().setName("activeUsers").build())
                .addDateRanges(DateRange.newBuilder().setStartDate("2024-10-01").setEndDate("today").build())
                .build();

        RunReportResponse response = betaAnalyticsDataClient.runReport(request);

        // Kiểm tra nếu response trả về lỗi hoặc không có dòng nào
        if (response == null || response.getRowsList().isEmpty()) {
            return 0;
        }

        // Sử dụng Stream API để tính tổng số lượng active users
        return response.getRowsList().stream()
                .mapToLong(row -> Long.parseLong(row.getMetricValues(0).getValue()))
                .sum();
    }
}


