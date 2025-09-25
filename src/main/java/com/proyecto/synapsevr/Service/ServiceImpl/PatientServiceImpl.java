package com.proyecto.synapsevr.Service.ServiceImpl;

import com.proyecto.synapsevr.dto.Request.PatientRequest;
import com.proyecto.synapsevr.dto.Response.PatientResponse;
import com.proyecto.synapsevr.Entity.PatientEntity;
import com.proyecto.synapsevr.Entity.UserEntity;
import com.proyecto.synapsevr.Repository.PatientRepository;
import com.proyecto.synapsevr.Repository.UserRepository;
import com.proyecto.synapsevr.Service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    
    @Override
    public List<PatientResponse> getAllPatients() {
        // Obtener el usuario logueado
        UserEntity currentUser = getCurrentUser();
        
        // Obtener solo los pacientes de este terapeuta
        List<PatientEntity> patients = patientRepository.findByUser(currentUser);

        //Convertimos List<PatientEntity> → List<PatientResponse>
        return patients.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public PatientResponse getPatientById(int patientId) {
        UserEntity currentUser = getCurrentUser();
        
        PatientEntity patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        
        // Verificar que el paciente pertenece al terapeuta logueado (comparación con int)
        if (patient.getUser().getUserId() != currentUser.getUserId()) {
            throw new RuntimeException("No tienes permisos para ver este paciente");
        }
        
        return convertToResponse(patient);
    }
    
    @Override
    public PatientResponse createPatient(PatientRequest request) {
        // Obtener el usuario logueado (terapeuta)
        UserEntity currentUser = getCurrentUser();
        
        PatientEntity patient = new PatientEntity();
        patient.setPatientName(request.getPatientName());
        patient.setAge(request.getAge());
        patient.setGender(request.getGender());
        patient.setImageUrl(request.getImageUrl()); // Imagen por defecto
        patient.setUser(currentUser); // Asignar el user logueado

        PatientEntity savedPatient = patientRepository.save(patient);
        return convertToResponse(savedPatient);
    }
    
    @Override
    public PatientResponse updatePatient(int patientId, PatientRequest request) {
        UserEntity currentUser = getCurrentUser();
        
        PatientEntity patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        
        // Verificar que el paciente pertenece al terapeuta logueado (comparación con int)
        if (patient.getUser().getUserId() != currentUser.getUserId()) {
            throw new RuntimeException("No tienes permisos para actualizar este paciente");
        }
        
        // Actualizar los campos
        patient.setPatientName(request.getPatientName());
        patient.setAge(request.getAge());
        patient.setGender(request.getGender());
        
        PatientEntity updatedPatient = patientRepository.save(patient);
        return convertToResponse(updatedPatient);
    }

    @Override
    public void deletePatient(int patientId) {
        System.out.println("🔍 Intentando eliminar paciente ID: " + patientId);

        UserEntity currentUser = getCurrentUser();
        System.out.println("🔍 Usuario actual: " + currentUser.getEmail());

        PatientEntity patient = patientRepository.findById(patientId)
                .orElseThrow(() -> {
                    System.out.println("❌ Paciente con ID " + patientId + " no encontrado");
                    return new RuntimeException("Paciente no encontrado");
                });

        System.out.println("🔍 Paciente encontrado: " + patient.getPatientName());
        System.out.println("🔍 Paciente pertenece a usuario ID: " + patient.getUser().getUserId());
        System.out.println("🔍 Usuario actual ID: " + currentUser.getUserId());

        // Verificar que el paciente pertenece al terapeuta logueado
        if (patient.getUser().getUserId() != currentUser.getUserId()) {
            System.out.println("❌ El paciente no pertenece al usuario actual");
            throw new RuntimeException("No tienes permisos para eliminar este paciente");
        }

        System.out.println("✅ Eliminando paciente...");
        patientRepository.delete(patient);
        System.out.println("✅ Paciente eliminado exitosamente");
    }
    
    /**
     * Método helper para obtener el usuario logueado
     */
    private UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        
        return userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    /**
     * Método helper para convertir Entity a Response DTO
     */
    private PatientResponse convertToResponse(PatientEntity entity) {
        return new PatientResponse(
                entity.getPatientId(),
                entity.getPatientName(),
                entity.getAge(),
                entity.getGender(),
                entity.getImageUrl(),
                entity.getUser().getUsername(), // Nombre del terapeuta
                entity.getSessions() != null ? entity.getSessions().size() : 0 // Total de sesiones
        );
    }
}