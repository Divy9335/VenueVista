package com.example.ticketing.service;

import com.example.ticketing.entity.Booking;
import com.example.ticketing.entity.Event;
import com.example.ticketing.entity.Ticket;
import com.example.ticketing.repository.BookingRepository;
import com.example.ticketing.repository.EventRepository;
import com.example.ticketing.repository.TicketRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;

    public AnalyticsService(EventRepository eventRepository,
                            BookingRepository bookingRepository,
                            TicketRepository ticketRepository) {
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
    }

    public Map<String, Object> getOverview() {
        List<Event> events = eventRepository.findAll();
        List<Booking> bookings = bookingRepository.findAll();
        List<Ticket> tickets = ticketRepository.findAll();
        double totalRevenue = bookings.stream().mapToDouble(Booking::getTotalAmount).sum();

        Map<String, Object> overview = new HashMap<>();
        overview.put("totalEvents", events.size());
        overview.put("totalBookings", bookings.size());
        overview.put("totalTicketsSold", tickets.size());
        overview.put("totalRevenue", totalRevenue);
        overview.put("averageBookingValue", bookings.isEmpty() ? 0.0 : totalRevenue / bookings.size());
        return overview;
    }

    public Map<String, Object> getRevenue() {
        double totalRevenue = bookingRepository.findAll().stream().mapToDouble(Booking::getTotalAmount).sum();
        Map<String, Object> response = new HashMap<>();
        response.put("totalRevenue", totalRevenue);
        return response;
    }

    public List<Map<String, Object>> getTicketSales() {
        return eventRepository.findAll().stream().map(event -> {
            Map<String, Object> row = new HashMap<>();
            long count = ticketRepository.findByEventSeat_EventId(event.getId()).size();
            row.put("eventId", event.getId());
            row.put("eventTitle", event.getTitle());
            row.put("ticketsSold", count);
            return row;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getPopularEvents() {
        return eventRepository.findAll().stream().map(event -> {
            Map<String, Object> row = new HashMap<>();
            long count = ticketRepository.findByEventSeat_EventId(event.getId()).size();
            row.put("eventId", event.getId());
            row.put("eventTitle", event.getTitle());
            row.put("totalSales", count);
            row.put("revenue", count * event.getBasePrice());
            return row;
        }).sorted((a, b) -> ((Long) b.get("totalSales")).compareTo((Long) a.get("totalSales")))
          .collect(Collectors.toList());
    }
}
