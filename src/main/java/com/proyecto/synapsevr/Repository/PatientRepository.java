package com.proyecto.synapsevr.Repository;

import com.proyecto.synapsevr.Entity.PatientEntity;
import com.proyecto.synapsevr.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<PatientEntity, Integer> {

    List<PatientEntity> findByUser(UserEntity user);
}
