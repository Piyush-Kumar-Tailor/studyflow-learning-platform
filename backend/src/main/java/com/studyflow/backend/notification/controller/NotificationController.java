package com.studyflow.backend.notification.controller;

import com.studyflow.backend.notification.dto.NotificationResponse;
import com.studyflow.backend.notification.dto.NotificationUnreadCountResponse;
import com.studyflow.backend.notification.service.NotificationService;
import com.studyflow.backend.user.entity.AppUser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/me/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Get all notifications
    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal AppUser user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = createPageable(page, size);

        return ResponseEntity.ok(
                notificationService.getMyNotifications(
                        user.getId(),
                        pageable
                )
        );
    }

    // Get unread notifications
    @GetMapping("/unread")
    public ResponseEntity<Page<NotificationResponse>> getUnreadNotifications(
            @AuthenticationPrincipal AppUser user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = createPageable(page, size);

        return ResponseEntity.ok(
                notificationService.getUnreadNotifications(
                        user.getId(),
                        pageable
                )
        );
    }

    // Get unread notification count
    @GetMapping("/unread-count")
    public ResponseEntity<NotificationUnreadCountResponse> getUnreadCount(
            @AuthenticationPrincipal AppUser user
    ) {
        return ResponseEntity.ok(
                notificationService.getUnreadCount(user.getId())
        );
    }

    // Mark one notification as read
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @AuthenticationPrincipal AppUser user,
            @PathVariable Long notificationId
    ) {
        return ResponseEntity.ok(
                notificationService.markAsRead(
                        user.getId(),
                        notificationId
                )
        );
    }

    // Mark all notifications as read
    @PatchMapping("/read-all")
    public ResponseEntity<Integer> markAllAsRead(
            @AuthenticationPrincipal AppUser user
    ) {
        return ResponseEntity.ok(
                notificationService.markAllAsRead(user.getId())
        );
    }

    // Delete one notification
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @AuthenticationPrincipal AppUser user,
            @PathVariable Long notificationId
    ) {
        notificationService.deleteNotification(
                user.getId(),
                notificationId
        );

        return ResponseEntity.noContent().build();
    }

    // Pagination helper
    private Pageable createPageable(int page, int size) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        int safeSize = Math.min(Math.max(size, 1), 50);

        return PageRequest.of(
                page,
                safeSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }
}