package com.example.ticketing.controller;

import com.example.ticketing.dto.ApiResponse;
import com.example.ticketing.dto.BookingRequest;
import com.example.ticketing.dto.BookingResponse;
import com.example.ticketing.entity.Booking;
import com.example.ticketing.entity.User;
import com.example.ticketing.service.AuthService;
import com.example.ticketing.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BookingController {
    private final BookingService bookingService;
    private final AuthService authService;

    public BookingController(BookingService bookingService, AuthService authService) {
        this.bookingService = bookingService;
        this.authService = authService;
    }

    @PostMapping("/bookings")
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(@Valid @RequestBody BookingRequest request) {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.<BookingResponse>builder().success(true).message("Booking confirmed").data(bookingService.createBooking(request, currentUser)).build());
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<Booking>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<Booking>>builder().success(true).message("Bookings fetched").data(bookingService.findAll()).build());
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<ApiResponse<Booking>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<Booking>builder().success(true).message("Booking fetched").data(bookingService.findById(id)).build());
    }

    @GetMapping("/users/{userId}/bookings")
    public ResponseEntity<ApiResponse<List<Booking>>> getUserBookings(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.<List<Booking>>builder().success(true).message("User bookings fetched").data(bookingService.findByUserId(userId)).build());
    }

    @PutMapping("/bookings/{id}/cancel")
    public ResponseEntity<ApiResponse<Booking>> cancelBooking(@PathVariable Long id) {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.<Booking>builder().success(true).message("Booking cancelled").data(bookingService.cancelBooking(id, currentUser)).build());
    }
}
