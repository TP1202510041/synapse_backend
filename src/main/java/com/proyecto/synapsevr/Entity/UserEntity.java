package com.proyecto.synapsevr.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;
    private String email;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "user_password")
    private String userPassword;

    @OneToMany(mappedBy = "user")
    private List<PatientEntity> patients;

    @OneToMany(mappedBy = "user")
    private List<SessionEntity> sessions;
}
