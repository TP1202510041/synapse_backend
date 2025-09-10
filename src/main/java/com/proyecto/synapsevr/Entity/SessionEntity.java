package com.proyecto.synapsevr.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "sessions")
public class SessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_session")
    private UUID idSession;
    @Column(name = "session_date")
    private LocalDateTime sessionDate;
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id_fk")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "patient_id_fk")
    private PatientEntity patient;
}
