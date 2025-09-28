package com.proyecto.synapsevr.Controller;

import com.proyecto.synapsevr.dto.Request.PatientRequest;
import com.proyecto.synapsevr.dto.Response.PatientResponse;
import com.proyecto.synapsevr.Service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Patients", description = "API para gestión de pacientes")
@SecurityRequirement(name = "Bearer Authentication")
public class PatientController {
    
    private final PatientService patientService;
    
    @GetMapping
    @Operation(summary = "Obtener todos los pacientes", description = "Obtiene la lista de todos los pacientes del terapeuta logueado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pacientes obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        try {
            List<PatientResponse> patients = patientService.getAllPatients();
            return ResponseEntity.ok(patients);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener paciente por ID", description = "Obtiene un paciente específico por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Paciente encontrado"),
        @ApiResponse(responseCode = "404", description = "Paciente no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<PatientResponse> getPatientById(
            @Parameter(description = "ID del paciente") @PathVariable int id) {
        try {
            PatientResponse patient = patientService.getPatientById(id);
            return ResponseEntity.ok(patient);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    @Operation(summary = "Crear nuevo paciente", description = "Crea un nuevo paciente en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Paciente creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de paciente inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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
    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar paciente", description = "Actualiza los datos de un paciente existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Paciente actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de paciente inválidos"),
        @ApiResponse(responseCode = "404", description = "Paciente no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<PatientResponse> updatePatient(
            @Parameter(description = "ID del paciente") @PathVariable int id, 
            @RequestBody PatientRequest request) {
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
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar paciente", description = "Elimina un paciente del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Paciente eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Paciente no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> deletePatient(
            @Parameter(description = "ID del paciente") @PathVariable int id) {
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