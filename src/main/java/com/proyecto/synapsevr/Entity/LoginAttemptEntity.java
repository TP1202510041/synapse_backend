package com.proyecto.synapsevr.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttemptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attempt_id")
    private Long attemptId;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "successful", nullable = false)
    private Boolean successful = false;

    @Column(name = "attempt_time", nullable = false)
    private LocalDateTime attemptTime;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;
}
