package com.proyecto.synapsevr.Service;

import com.proyecto.synapsevr.dto.Request.EventRequest;
import com.proyecto.synapsevr.dto.Response.EventResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    
    EventResponse createEvent(EventRequest request, Integer userId);
    
    EventResponse updateEvent(Integer eventId, EventRequest request, Integer userId);
    
    void deleteEvent(Integer eventId, Integer userId);
    
    EventResponse getEventById(Integer eventId, Integer userId);
    
    List<EventResponse> getAllEventsByUser(Integer userId);
    
    List<EventResponse> getEventsByDateRange(Integer userId, LocalDateTime startDate, LocalDateTime endDate);
}