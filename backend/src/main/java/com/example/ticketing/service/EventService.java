package com.example.ticketing.service;

import com.example.ticketing.dto.EventRequest;
import com.example.ticketing.entity.Event;
import com.example.ticketing.entity.EventSeat;
import com.example.ticketing.entity.Seat;
import com.example.ticketing.entity.Venue;
import com.example.ticketing.enums.SeatStatus;
import com.example.ticketing.exception.BadRequestException;
import com.example.ticketing.exception.ResourceNotFoundException;
import com.example.ticketing.repository.EventRepository;
import com.example.ticketing.repository.EventSeatRepository;
import com.example.ticketing.repository.SeatRepository;
import com.example.ticketing.repository.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;
    private final EventSeatRepository eventSeatRepository;

    public EventService(EventRepository eventRepository,
                        VenueRepository venueRepository,
                        SeatRepository seatRepository,
                        EventSeatRepository eventSeatRepository) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.seatRepository = seatRepository;
        this.eventSeatRepository = eventSeatRepository;
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
    }

    @Transactional
    public Event create(EventRequest request) {
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new BadRequestException("Event start time must be before end time");
        }

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found: " + request.getVenueId()));

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .basePrice(request.getBasePrice())
                .venue(venue)
                .build();

        Event savedEvent = eventRepository.save(event);
        List<Seat> seats = seatRepository.findByVenueId(venue.getId());
        for (Seat seat : seats) {
            EventSeat eventSeat = EventSeat.builder()
                    .event(savedEvent)
                    .seat(seat)
                    .status(SeatStatus.AVAILABLE)
                    .price(request.getBasePrice())
                    .build();
            savedEvent.getEventSeats().add(eventSeat);
        }
        return eventRepository.save(savedEvent);
    }

    @Transactional
    public Event update(Long id, EventRequest request) {
        Event event = findById(id);
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new BadRequestException("Event start time must be before end time");
        }
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setBasePrice(request.getBasePrice());
        return eventRepository.save(event);
    }

    @Transactional
    public void delete(Long id) {
        Event event = findById(id);
        eventRepository.delete(event);
    }
}
