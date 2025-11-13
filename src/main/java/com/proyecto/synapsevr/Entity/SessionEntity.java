package com.proyecto.synapsevr.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
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
    private LocalDate sessionDate;
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "exposure_level")
    private ExposureLevel exposureLevel = ExposureLevel.MEDIO;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id_fk")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "patient_id_fk")
    private PatientEntity patient;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClinicalObservationEntity> observations;

    @OneToOne(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private VrSessionEntity vrSession;

    public enum ExposureLevel {
        BAJO("Bajo"),
        MEDIO("Medio"),
        ALTO("Alto"),
        MUY_ALTO("Muy Alto");

        private final String displayName;

        ExposureLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
