package com.studyflow.backend.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "app_users",
        indexes = {
                @Index(name = "idx_app_user_email", columnList = "email", unique = true)
        }
)
public class AppUser {


    @
}
