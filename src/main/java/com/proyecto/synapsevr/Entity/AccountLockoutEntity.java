package com.proyecto.synapsevr.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "account_lockouts")
@NoArgsConstructor
@AllArgsConstructor
public class AccountLockoutEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "locked_at", nullable = false)
    private LocalDateTime lockedAt = LocalDateTime.now();

    @Column(name = "unlock_at", nullable = false)
    private LocalDateTime unlockAt;

    @Column(name = "reason", length = 100)
    private String reason;

    @Column(name = "unlocked_by")
    private Integer unlockedBy;
}