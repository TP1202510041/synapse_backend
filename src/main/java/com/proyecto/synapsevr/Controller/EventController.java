package com.proyecto.synapsevr.Controller;

import com.proyecto.synapsevr.Entity.UserEntity;
import com.proyecto.synapsevr.Repository.UserRepository;
import com.proyecto.synapsevr.Service.EventService;
import com.proyecto.synapsevr.dto.Request.CreateEventRequest;
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

import java.security.Principal;
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
    private final UserRepository userRepository;
    
    @GetMapping
    @Operation(summary = "Obtener todos los eventos", description = "Obtiene todos los eventos del usuario logueado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de eventos obtenida exitosamente")
    public ResponseEntity<List<EventResponse>> getAllEvents(Principal principal) {
        try {
            UserEntity user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Integer userId = user.getUserId();
            
            List<EventResponse> events = eventService.getAllEventsByUser(userId);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo eventos: " + e.getMessage());
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
            @Parameter(description = "ID del evento") @PathVariable Integer id,
            Principal principal) {
        try {
            UserEntity user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Integer userId = user.getUserId();
            
            EventResponse event = eventService.getEventById(id, userId);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            System.err.println("❌ Evento no encontrado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo evento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/range")
    @Operation(summary = "Obtener eventos por rango de fechas", description = "Obtiene eventos dentro de un rango de fechas específico")
    public ResponseEntity<List<EventResponse>> getEventsByDateRange(
            @Parameter(description = "Fecha de inicio") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Fecha de fin") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Principal principal) {
        try {
            UserEntity user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Integer userId = user.getUserId();
            
            List<EventResponse> events = eventService.getEventsByDateRange(userId, startDate, endDate);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo eventos por rango: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    @Operation(summary = "Crear nuevo evento", description = "Crea un nuevo evento en el calendario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Evento creado exitosamente")
    public ResponseEntity<EventResponse> createEvent(@RequestBody CreateEventRequest request, Principal principal) {
        try {
            UserEntity user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Integer userId = user.getUserId();
            
            System.out.println("🎯 Creando evento para usuario ID: " + userId + " (" + principal.getName() + ")");
            
            // Convertir CreateEventRequest a EventRequest
            EventRequest eventRequest = new EventRequest();
            eventRequest.setTitle(request.getTitle());
            eventRequest.setStart(request.getStart());
            eventRequest.setEnd(request.getEnd());
            eventRequest.setDescription(null); // Opcional
            eventRequest.setColor("#4285f4"); // Color por defecto
            
            EventResponse createdEvent = eventService.createEvent(eventRequest, userId);
            System.out.println("✅ Evento creado exitosamente con ID: " + createdEvent.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
        } catch (Exception e) {
            System.err.println("❌ Error creando evento: " + e.getMessage());
            e.printStackTrace();
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
            @RequestBody EventRequest request,
            Principal principal) {
        try {
            UserEntity user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Integer userId = user.getUserId();
            
            System.out.println("🔄 Actualizando evento ID: " + id + " para usuario: " + userId);
            EventResponse updatedEvent = eventService.updateEvent(id, request, userId);
            System.out.println("✅ Evento actualizado exitosamente");
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            System.err.println("❌ Evento no encontrado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            System.err.println("❌ Error actualizando evento: " + e.getMessage());
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
            @Parameter(description = "ID del evento") @PathVariable Integer id,
            Principal principal) {
        try {
            UserEntity user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Integer userId = user.getUserId();
            
            System.out.println("🗑️ Eliminando evento ID: " + id + " para usuario: " + userId);
            eventService.deleteEvent(id, userId);
            System.out.println("✅ Evento eliminado exitosamente");
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            System.err.println("❌ Evento no encontrado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            System.err.println("❌ Error eliminando evento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}