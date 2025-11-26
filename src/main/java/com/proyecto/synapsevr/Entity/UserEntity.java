package com.proyecto.synapsevr.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "user_password", nullable = false)
    private String userPassword;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "dni", unique = true, length = 20)
    private String dni;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender", length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "professional_license", length = 50)
    private String professionalLicense;

    @Column(name = "specialization", length = 100)
    private String specialization;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(name = "account_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Implementación de UserDetails para Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return userPassword;
    }

    @Override
    public String getUsername() {
        return email; // Usamos email como username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Getter manual para userName (por si Lombok no lo genera)
    public String getUserName() {
        return userName;
    }

    // Enum para roles
    public enum Role {
        USER, ADMIN, THERAPIST, SUPERVISOR
    }

    // Enum para género
    public enum Gender {
        MASCULINO, FEMENINO, OTRO, PREFIERO_NO_DECIR
    }

    // Enum para estado de cuenta
    public enum AccountStatus {
        ACTIVE, LOCKED, SUSPENDED, PENDING_VERIFICATION, DISABLED
    }

    // Método para verificar si la cuenta está bloqueada
    public boolean isAccountLocked() {
        return accountStatus == AccountStatus.LOCKED || 
               (accountLockedUntil != null && accountLockedUntil.isAfter(LocalDateTime.now()));
    }

    // Método para verificar si necesita cambiar contraseña
    public boolean needsPasswordChange() {
        if (passwordChangedAt == null) return true;
        return passwordChangedAt.isBefore(LocalDateTime.now().minusMonths(6));
    }

    // Método para incrementar intentos fallidos
    public void incrementFailedAttempts() {
        this.failedLoginAttempts = (this.failedLoginAttempts == null ? 0 : this.failedLoginAttempts) + 1;
        if (this.failedLoginAttempts >= 5) {
            this.accountStatus = AccountStatus.LOCKED;
            this.accountLockedUntil = LocalDateTime.now().plusHours(1); // Bloquear por 1 hora
        }
    }

    // Método para resetear intentos fallidos
    public void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
        if (this.accountStatus == AccountStatus.LOCKED && 
            (this.accountLockedUntil == null || this.accountLockedUntil.isBefore(LocalDateTime.now()))) {
            this.accountStatus = AccountStatus.ACTIVE;
            this.accountLockedUntil = null;
        }
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isAccountLocked();
    }

    @Override
    public boolean isEnabled() {
        return accountStatus == AccountStatus.ACTIVE || accountStatus == AccountStatus.PENDING_VERIFICATION;
    }
}