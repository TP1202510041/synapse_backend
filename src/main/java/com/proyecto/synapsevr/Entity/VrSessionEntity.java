package com.proyecto.synapsevr.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "vr_sessions")
@NoArgsConstructor
@AllArgsConstructor
public class VrSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "vr_scenario", nullable = false, length = 100)
    private String vrScenario;

    @Column(name = "vr_device", length = 100)
    private String vrDevice;

    @Column(name = "immersion_duration")
    private Integer immersionDuration;

    @Column(name = "movement_tracking_data", columnDefinition = "JSON")
    private String movementTrackingData;

    @Column(name = "environment_settings", columnDefinition = "JSON")
    private String environmentSettings;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    private SessionEntity session;
}