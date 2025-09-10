package com.proyecto.synapsevr.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "patients")
public class PatientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private int patientId;
    @Column(name = "patient_name")
    private String patientName;
    private int age;
    private String gender;
    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "user_id_fk")
    private UserEntity user;

    @OneToMany(mappedBy = "patient")
    private List<SessionEntity> sessions;
}
