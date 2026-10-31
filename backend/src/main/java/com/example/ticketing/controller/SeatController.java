package com.example.ticketing.controller;

import com.example.ticketing.dto.ApiResponse;
import com.example.ticketing.dto.SeatSummary;
import com.example.ticketing.service.SeatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SeatController {
    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping("/venues/{venueId}/seats")
    public ResponseEntity<ApiResponse<List<SeatSummary>>> getVenueSeats(@PathVariable Long venueId) {
        return ResponseEntity.ok(ApiResponse.<List<SeatSummary>>builder().success(true).message("Venue seats fetched").data(seatService.getVenueSeats(venueId)).build());
    }

    @GetMapping("/events/{eventId}/seats")
    public ResponseEntity<ApiResponse<List<SeatSummary>>> getEventSeats(@PathVariable Long eventId) {
        return ResponseEntity.ok(ApiResponse.<List<SeatSummary>>builder().success(true).message("Event seats fetched").data(seatService.getEventSeats(eventId)).build());
    }

    @GetMapping("/events/{eventId}/available-seats")
    public ResponseEntity<ApiResponse<List<SeatSummary>>> getAvailableSeats(@PathVariable Long eventId) {
        return ResponseEntity.ok(ApiResponse.<List<SeatSummary>>builder().success(true).message("Available seats fetched").data(seatService.getAvailableSeats(eventId)).build());
    }
}
