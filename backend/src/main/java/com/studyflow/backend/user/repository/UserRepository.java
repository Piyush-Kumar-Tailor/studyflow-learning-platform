package com.studyflow.backend.user.repository;



import com.studyflow.backend.user.entity.AppUser;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.studyflow.backend.user.enums.Role;

import org.springframework.data.domain.Pageable;
import java.util.List;

import com.studyflow.backend.admin.dto.DailyRegistrationProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRole(Role role);

    long countByEnabledTrue();

    long countByRoleAndEnabledTrue(Role role);

    List<AppUser> findByRoleOrderByCreatedAtDesc(
            Role role,
            Pageable pageable
    );

    @Query(value = """
        SELECT
            CAST(created_at AS DATE) AS activity_date,
            COUNT(*) AS total_registrations
        FROM app_users
        WHERE created_at >= CURRENT_DATE - INTERVAL '6 days'
          AND created_at < CURRENT_DATE + INTERVAL '1 day'
          AND role = 'STUDENT'
        GROUP BY CAST(created_at AS DATE)
        ORDER BY activity_date ASC
        """, nativeQuery = true)
    List<DailyRegistrationProjection> findDailyStudentRegistrations();


    @Query("""
    SELECT u FROM AppUser u
    WHERE (
        :keyword IS NULL
        OR LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
    AND (:role IS NULL OR u.role = :role)
    """)
    Page<AppUser> searchUsers(
            @Param("keyword") String keyword,
            @Param("role") Role role,
            Pageable pageable
    );

    List<AppUser> findByRoleAndEnabledTrueOrderByCreatedAtDesc(
            Role role
    );




}

