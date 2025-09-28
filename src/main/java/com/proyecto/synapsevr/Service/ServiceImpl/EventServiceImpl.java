package com.proyecto.synapsevr.Service.ServiceImpl;

import com.proyecto.synapsevr.Entity.EventEntity;
import com.proyecto.synapsevr.Repository.EventRepository;
import com.proyecto.synapsevr.Service.EventService;
import com.proyecto.synapsevr.dto.Request.EventRequest;
import com.proyecto.synapsevr.dto.Response.EventResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    @Transactional
    public EventResponse createEvent(EventRequest request, Integer userId) {
        log.info("Creando evento para usuario: {}", userId);
        
        EventEntity entity = new EventEntity();
        entity.setUserId(userId);
        entity.setTitle(request.getTitle());
        entity.setStart(request.getStart());
        entity.setEnd(request.getEnd());
        entity.setDescription(request.getDescription());
        entity.setColor(request.getColor() != null ? request.getColor() : "#4285f4");
        
        EventEntity savedEntity = eventRepository.save(entity);
        log.info("Evento creado con ID: {}", savedEntity.getId());
        
        return new EventResponse(savedEntity);
    }

    @Override
    @Transactional
    public EventResponse updateEvent(Integer eventId, EventRequest request, Integer userId) {
        log.info("Actualizando evento {} para usuario: {}", eventId, userId);
        
        EventEntity entity = eventRepository.findByIdAndUserId(eventId, userId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado o no pertenece al usuario"));
        
        entity.setTitle(request.getTitle());
        entity.setStart(request.getStart());
        entity.setEnd(request.getEnd());
        entity.setDescription(request.getDescription());
        entity.setColor(request.getColor() != null ? request.getColor() : entity.getColor());
        
        EventEntity savedEntity = eventRepository.save(entity);
        log.info("Evento actualizado: {}", eventId);
        
        return new EventResponse(savedEntity);
    }

    @Override
    @Transactional
    public void deleteEvent(Integer eventId, Integer userId) {
        log.info("Eliminando evento {} para usuario: {}", eventId, userId);
        
        EventEntity entity = eventRepository.findByIdAndUserId(eventId, userId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado o no pertenece al usuario"));
        
        eventRepository.delete(entity);
        log.info("Evento eliminado: {}", eventId);
    }

    @Override
    public EventResponse getEventById(Integer eventId, Integer userId) {
        log.info("Obteniendo evento {} para usuario: {}", eventId, userId);
        
        EventEntity entity = eventRepository.findByIdAndUserId(eventId, userId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado o no pertenece al usuario"));
        
        return new EventResponse(entity);
    }

    @Override
    public List<EventResponse> getAllEventsByUser(Integer userId) {
        log.info("Obteniendo todos los eventos para usuario: {}", userId);
        
        List<EventEntity> entities = eventRepository.findByUserId(userId);
        
        return entities.stream()
                .map(EventResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventResponse> getEventsByDateRange(Integer userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Obteniendo eventos para usuario: {} entre {} y {}", userId, startDate, endDate);
        
        List<EventEntity> entities = eventRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        
        return entities.stream()
                .map(EventResponse::new)
                .collect(Collectors.toList());
    }
}