package com.proyecto.synapsevr.Repository;

import com.proyecto.synapsevr.Entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<PatientEntity, Integer> {
}
