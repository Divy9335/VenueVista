package com.example.ticketing.controller;

import com.example.ticketing.dto.ApiResponse;
import com.example.ticketing.dto.EventRequest;
import com.example.ticketing.entity.Event;
import com.example.ticketing.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/events")
    public ResponseEntity<ApiResponse<Event>> create(@Valid @RequestBody EventRequest request) {
        Event event = eventService.create(request);
        return ResponseEntity.ok(ApiResponse.<Event>builder().success(true).message("Event created").data(event).build());
    }

    @GetMapping("/events")
    public ResponseEntity<ApiResponse<List<Event>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<Event>>builder().success(true).message("Events fetched").data(eventService.findAll()).build());
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<ApiResponse<Event>> getById(@PathVariable Long id) {
        Event event = eventService.findById(id);
        return ResponseEntity.ok(ApiResponse.<Event>builder().success(true).message("Event fetched").data(event).build());
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<ApiResponse<Event>> update(@PathVariable Long id, @Valid @RequestBody EventRequest request) {
        Event event = eventService.update(id, request);
        return ResponseEntity.ok(ApiResponse.<Event>builder().success(true).message("Event updated").data(event).build());
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Event deleted").data(null).build());
    }
}
