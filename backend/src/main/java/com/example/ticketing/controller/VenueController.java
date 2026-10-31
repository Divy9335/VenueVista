package com.example.ticketing.controller;

import com.example.ticketing.dto.ApiResponse;
import com.example.ticketing.dto.VenueRequest;
import com.example.ticketing.entity.Venue;
import com.example.ticketing.service.VenueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class VenueController {
    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @PostMapping("/venues")
    public ResponseEntity<ApiResponse<Venue>> create(@Valid @RequestBody VenueRequest request) {
        return ResponseEntity.ok(ApiResponse.<Venue>builder().success(true).message("Venue created").data(venueService.create(request)).build());
    }

    @GetMapping("/venues")
    public ResponseEntity<ApiResponse<List<Venue>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<Venue>>builder().success(true).message("Venues fetched").data(venueService.findAll()).build());
    }

    @GetMapping("/venues/{id}")
    public ResponseEntity<ApiResponse<Venue>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<Venue>builder().success(true).message("Venue fetched").data(venueService.findById(id)).build());
    }

    @PutMapping("/venues/{id}")
    public ResponseEntity<ApiResponse<Venue>> update(@PathVariable Long id, @Valid @RequestBody VenueRequest request) {
        return ResponseEntity.ok(ApiResponse.<Venue>builder().success(true).message("Venue updated").data(venueService.update(id, request)).build());
    }

    @DeleteMapping("/venues/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        venueService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Venue deleted").data(null).build());
    }
}
