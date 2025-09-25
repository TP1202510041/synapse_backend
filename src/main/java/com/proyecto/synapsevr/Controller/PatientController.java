package com.proyecto.synapsevr.Controller;

import com.proyecto.synapsevr.dto.Request.PatientRequest;
import com.proyecto.synapsevr.dto.Response.PatientResponse;
import com.proyecto.synapsevr.Service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PatientController {
    
    private final PatientService patientService;
    
    /**
     * Obtener todos los pacientes del terapeuta logueado
     * GET /api/patients
     */
    @GetMapping
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        try {
            List<PatientResponse> patients = patientService.getAllPatients();
            return ResponseEntity.ok(patients);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener un paciente por ID
     * GET /api/patients/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable int id) {
        try {
            PatientResponse patient = patientService.getPatientById(id);
            return ResponseEntity.ok(patient);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Crear un nuevo paciente
     * POST /api/patients
     */
    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@RequestBody PatientRequest request) {
        try {
            // Validar que los campos obligatorios no estén vacíos
            if (request.getPatientName() == null || request.getPatientName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (request.getAge() <= 0) {
                return ResponseEntity.badRequest().build();
            }
            if (request.getGender() == null || request.getGender().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            PatientResponse createdPatient = patientService.createPatient(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Actualizar un paciente existente
     * PUT /api/patients/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(@PathVariable int id, @RequestBody PatientRequest request) {
        try {
            // Validar que los campos obligatorios no estén vacíos
            if (request.getPatientName() == null || request.getPatientName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (request.getAge() <= 0) {
                return ResponseEntity.badRequest().build();
            }
            if (request.getGender() == null || request.getGender().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            PatientResponse updatedPatient = patientService.updatePatient(id, request);
            return ResponseEntity.ok(updatedPatient);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Eliminar un paciente
     * DELETE /api/patients/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable int id) {
        try {
            patientService.deletePatient(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}