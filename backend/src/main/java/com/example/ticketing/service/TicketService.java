package com.example.ticketing.service;

import com.example.ticketing.entity.Ticket;
import com.example.ticketing.exception.ResourceNotFoundException;
import com.example.ticketing.repository.TicketRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    public Ticket findById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + id));
    }

    public List<Ticket> findByBookingId(Long bookingId) {
        return ticketRepository.findByBookingId(bookingId);
    }
}
