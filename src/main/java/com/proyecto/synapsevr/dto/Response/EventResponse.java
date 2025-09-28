package com.proyecto.synapsevr.dto.Response;

import com.proyecto.synapsevr.Entity.EventEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventResponse {
    private Integer id;
    private Integer userId;
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private String description;
    private String color;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public EventResponse(EventEntity entity) {
        this.id = entity.getId();
        this.userId = entity.getUserId();
        this.title = entity.getTitle();
        this.start = entity.getStart();
        this.end = entity.getEnd();
        this.description = entity.getDescription();
        this.color = entity.getColor();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }
}