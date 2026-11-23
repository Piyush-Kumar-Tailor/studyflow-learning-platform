
package com.studyflow.backend.admin.entity;

import com.studyflow.backend.user.entity.AppUser;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_audit_logs")
public class AdminAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private AppUser admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private AppUser targetUser;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(name = "old_value", length = 100)
    private String oldValue;

    @Column(name = "new_value", length = 100)
    private String newValue;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public AdminAuditLog() {
    }

    public AdminAuditLog(
            AppUser admin,
            AppUser targetUser,
            String action,
            String oldValue,
            String newValue
    ) {
        this.admin = admin;
        this.targetUser = targetUser;
        this.action = action;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public AppUser getAdmin() {
        return admin;
    }

    public AppUser getTargetUser() {
        return targetUser;
    }

    public String getAction() {
        return action;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
