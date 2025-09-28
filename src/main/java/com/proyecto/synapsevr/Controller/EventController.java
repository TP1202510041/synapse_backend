package com.proyecto.synapsevr.Controller;

import com.proyecto.synapsevr.Service.EventService;
import com.proyecto.synapsevr.dto.Request.EventRequest;
import com.proyecto.synapsevr.dto.Response.EventResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Events", description = "API para gestión de eventos del calendario")
@SecurityRequirement(name = "Bearer Authentication")
public class EventController {
    
    private final EventService eventService;
    
    @GetMapping
    @Operation(summary = "Obtener todos los eventos", description = "Obtiene todos los eventos del usuario logueado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de eventos obtenida exitosamente")
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        try {
            // TODO: Obtener userId del JWT token
            Integer userId = 1; // Temporal
            List<EventResponse> events = eventService.getAllEventsByUser(userId);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener evento por ID", description = "Obtiene un evento específico por su ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Evento encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    public ResponseEntity<EventResponse> getEventById(
            @Parameter(description = "ID del evento") @PathVariable Integer id) {
        try {
            Integer userId = 1; // Temporal
            EventResponse event = eventService.getEventById(id, userId);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/range")
    @Operation(summary = "Obtener eventos por rango de fechas", description = "Obtiene eventos dentro de un rango de fechas específico")
    public ResponseEntity<List<EventResponse>> getEventsByDateRange(
            @Parameter(description = "Fecha de inicio") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Fecha de fin") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            Integer userId = 1; // Temporal
            List<EventResponse> events = eventService.getEventsByDateRange(userId, startDate, endDate);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    @Operation(summary = "Crear nuevo evento", description = "Crea un nuevo evento en el calendario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Evento creado exitosamente")
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventRequest request) {
        try {
            Integer userId = 1; // Temporal
            EventResponse createdEvent = eventService.createEvent(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar evento", description = "Actualiza un evento existente")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Evento actualizado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    public ResponseEntity<EventResponse> updateEvent(
            @Parameter(description = "ID del evento") @PathVariable Integer id,
            @RequestBody EventRequest request) {
        try {
            Integer userId = 1; // Temporal
            EventResponse updatedEvent = eventService.updateEvent(id, request, userId);
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar evento", description = "Elimina un evento del calendario")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Evento eliminado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    public ResponseEntity<Void> deleteEvent(
            @Parameter(description = "ID del evento") @PathVariable Integer id) {
        try {
            Integer userId = 1; // Temporal
            eventService.deleteEvent(id, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}