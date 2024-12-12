package com.example.course_be.service.impl;

import com.google.analytics.data.v1beta.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;


@Service
public class GoogleAnalyticsService {

    private final BetaAnalyticsDataClient betaAnalyticsDataClient;

    @Value("${google.analytics.property-id}")
    private String propertyId;

    public GoogleAnalyticsService(BetaAnalyticsDataClient betaAnalyticsDataClient) {
        this.betaAnalyticsDataClient = betaAnalyticsDataClient;
    }

    public long getAccessSessions() throws IOException {
        String today = LocalDate.now().toString();
        DateRange dateRange = DateRange.newBuilder()
                .setStartDate("2024-10-01")
                .setEndDate(today)
                .build();

        // RunReportRequest lấy dữ liệu báo cáo từ Google Analytics
        RunReportRequest request = RunReportRequest.newBuilder()
                .setProperty("properties/" + propertyId)
                .addMetrics(Metric.newBuilder().setName("sessions").build())
                .addDateRanges(dateRange)
                .build();

        RunReportResponse response = betaAnalyticsDataClient.runReport(request);

        // Kiểm tra nếu response trả về lỗi hoặc không có dòng nào
        if (response == null || response.getRowsList().isEmpty()) {
            return 0;
        }

        return response.getRowsList().stream()
                .peek(row -> System.out.println("Processing row: " + row))
                .mapToLong(row -> Long.parseLong(row.getMetricValues(0).getValue()))
                .sum();
    }
}


