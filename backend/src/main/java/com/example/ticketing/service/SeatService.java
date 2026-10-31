package com.example.ticketing.service;

import com.example.ticketing.dto.SeatSummary;
import com.example.ticketing.entity.Event;
import com.example.ticketing.entity.EventSeat;
import com.example.ticketing.entity.Seat;
import com.example.ticketing.entity.Venue;
import com.example.ticketing.exception.ResourceNotFoundException;
import com.example.ticketing.repository.EventRepository;
import com.example.ticketing.repository.EventSeatRepository;
import com.example.ticketing.repository.SeatRepository;
import com.example.ticketing.repository.VenueRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SeatService {
    private final SeatRepository seatRepository;
    private final EventSeatRepository eventSeatRepository;
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;

    public SeatService(SeatRepository seatRepository,
                       EventSeatRepository eventSeatRepository,
                       EventRepository eventRepository,
                       VenueRepository venueRepository) {
        this.seatRepository = seatRepository;
        this.eventSeatRepository = eventSeatRepository;
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
    }

    public List<SeatSummary> getVenueSeats(Long venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found: " + venueId));
        return seatRepository.findByVenueId(venueId).stream()
                .map(seat -> SeatSummary.builder()
                        .seatId(seat.getId())
                        .rowLabel(seat.getRowLabel())
                        .seatNumber(seat.getSeatNumber())
                        .price(seat.getPrice())
                        .build())
                .toList();
    }

    public List<SeatSummary> getEventSeats(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + eventId));
        return eventSeatRepository.findByEventId(eventId).stream()
                .map(es -> SeatSummary.builder()
                        .eventSeatId(es.getId())
                        .seatId(es.getSeat().getId())
                        .rowLabel(es.getSeat().getRowLabel())
                        .seatNumber(es.getSeat().getSeatNumber())
                        .price(es.getPrice())
                        .status(es.getStatus())
                        .build())
                .toList();
    }

    public List<SeatSummary> getAvailableSeats(Long eventId) {
        return eventSeatRepository.findByEventId(eventId).stream()
                .filter(es -> es.getStatus().name().equals("AVAILABLE"))
                .map(es -> SeatSummary.builder()
                        .eventSeatId(es.getId())
                        .seatId(es.getSeat().getId())
                        .rowLabel(es.getSeat().getRowLabel())
                        .seatNumber(es.getSeat().getSeatNumber())
                        .price(es.getPrice())
                        .status(es.getStatus())
                        .build())
                .toList();
    }
}
