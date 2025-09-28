package com.proyecto.synapsevr.dto.Request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventRequest {
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private String description;
    private String color;
}