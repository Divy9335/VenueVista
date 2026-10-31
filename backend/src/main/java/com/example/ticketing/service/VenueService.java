package com.example.ticketing.service;

import com.example.ticketing.dto.VenueRequest;
import com.example.ticketing.entity.Seat;
import com.example.ticketing.entity.Venue;
import com.example.ticketing.exception.ResourceNotFoundException;
import com.example.ticketing.repository.SeatRepository;
import com.example.ticketing.repository.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class VenueService {
    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;

    public VenueService(VenueRepository venueRepository, SeatRepository seatRepository) {
        this.venueRepository = venueRepository;
        this.seatRepository = seatRepository;
    }

    public List<Venue> findAll() {
        return venueRepository.findAll();
    }

    public Venue findById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found: " + id));
    }

    @Transactional
    public Venue create(VenueRequest request) {
        Venue venue = Venue.builder()
                .name(request.getName())
                .city(request.getCity())
                .address(request.getAddress())
                .capacity(request.getCapacity())
                .build();

        Venue saved = venueRepository.save(venue);
        List<Seat> seats = new ArrayList<>();
        char[] rows = {'A', 'B', 'C', 'D', 'E'};
        int maxSeatsPerRow = Math.max(4, request.getCapacity() / rows.length);
        for (int rowIndex = 0; rowIndex < rows.length && seats.size() < request.getCapacity(); rowIndex++) {
            for (int seatNum = 1; seatNum <= maxSeatsPerRow && seats.size() < request.getCapacity(); seatNum++) {
                Seat seat = Seat.builder()
                        .venue(saved)
                        .rowLabel(String.valueOf(rows[rowIndex]))
                        .seatNumber(seatNum)
                        .price(120.0)
                        .build();
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);
        saved.setSeats(seats);
        return saved;
    }

    @Transactional
    public Venue update(Long id, VenueRequest request) {
        Venue venue = findById(id);
        venue.setName(request.getName());
        venue.setCity(request.getCity());
        venue.setAddress(request.getAddress());
        venue.setCapacity(request.getCapacity());
        return venueRepository.save(venue);
    }

    @Transactional
    public void delete(Long id) {
        Venue venue = findById(id);
        venueRepository.delete(venue);
    }
}
