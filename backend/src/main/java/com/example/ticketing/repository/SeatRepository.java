package com.example.ticketing.repository;

import com.example.ticketing.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByVenueId(Long venueId);
    Optional<Seat> findByVenueIdAndRowLabelAndSeatNumber(Long venueId, String rowLabel, Integer seatNumber);
}
