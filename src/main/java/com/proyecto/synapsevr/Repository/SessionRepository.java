package com.proyecto.synapsevr.Repository;

import com.proyecto.synapsevr.Entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SessionRepository extends JpaRepository<SessionEntity, UUID> {
}
