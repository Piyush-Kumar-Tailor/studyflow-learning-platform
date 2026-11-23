package com.studyflow.backend.admin.dto;

import java.util.Map;

public record AdminNotificationAnalyticsResponse(
        long totalNotifications,
        long readNotifications,
        long unreadNotifications,
        double readPercentage,
        Map<String, Long> notificationTypeDistribution
) {
}