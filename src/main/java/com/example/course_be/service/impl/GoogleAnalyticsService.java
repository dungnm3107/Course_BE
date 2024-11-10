package com.example.course_be.service.impl;

import com.google.analytics.data.v1beta.BetaAnalyticsDataClient;
import com.google.analytics.data.v1beta.RunReportRequest;
import com.google.analytics.data.v1beta.RunReportResponse;
import com.google.analytics.data.v1beta.DateRange;
import com.google.analytics.data.v1beta.Metric;
import com.google.analytics.data.v1beta.Dimension;
import com.google.analytics.data.v1beta.DimensionHeader;
import com.google.analytics.data.v1beta.MetricHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

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
                .setProperty("properties/" + propertyId)  // Chèn property ID
                .addDimensions(Dimension.newBuilder().setName("date").build()) // Thêm Dimension
                .addMetrics(Metric.newBuilder().setName("activeUsers").build()) // Thêm Metric
                .addDateRanges(DateRange.newBuilder().setStartDate("2024-10-01").setEndDate("today").build()) // Khoảng thời gian
                .build();


        RunReportResponse response = betaAnalyticsDataClient.runReport(request);

        if (!response.getRowsList().isEmpty()) {
            long totalActiveUsers = 0;

            // Duyệt qua tất cả các dòng và cộng tổng số lượng người dùng
            for (var row : response.getRowsList()) {
                totalActiveUsers += Long.parseLong(row.getMetricValues(0).getValue());
            }

            return totalActiveUsers;
        }
        return 0;
    }

}

