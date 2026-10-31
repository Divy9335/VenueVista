package com.example.ticketing.controller;

import com.example.ticketing.dto.ApiResponse;
import com.example.ticketing.entity.Ticket;
import com.example.ticketing.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/tickets")
    public ResponseEntity<ApiResponse<List<Ticket>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<Ticket>>builder().success(true).message("Tickets fetched").data(ticketService.findAll()).build());
    }

    @GetMapping("/tickets/{id}")
    public ResponseEntity<ApiResponse<Ticket>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<Ticket>builder().success(true).message("Ticket fetched").data(ticketService.findById(id)).build());
    }

    @GetMapping("/bookings/{bookingId}/tickets")
    public ResponseEntity<ApiResponse<List<Ticket>>> getByBookingId(@PathVariable Long bookingId) {
        return ResponseEntity.ok(ApiResponse.<List<Ticket>>builder().success(true).message("Booking tickets fetched").data(ticketService.findByBookingId(bookingId)).build());
    }
}
