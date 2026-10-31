package com.example.ticketing.service;

import com.example.ticketing.dto.BookingRequest;
import com.example.ticketing.dto.BookingResponse;
import com.example.ticketing.entity.*;
import com.example.ticketing.enums.BookingStatus;
import com.example.ticketing.enums.PaymentStatus;
import com.example.ticketing.enums.SeatStatus;
import com.example.ticketing.enums.TicketStatus;
import com.example.ticketing.exception.BadRequestException;
import com.example.ticketing.exception.ResourceNotFoundException;
import com.example.ticketing.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {
    private final EventRepository eventRepository;
    private final EventSeatRepository eventSeatRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;

    public BookingService(EventRepository eventRepository,
                          EventSeatRepository eventSeatRepository,
                          BookingRepository bookingRepository,
                          TicketRepository ticketRepository,
                          PaymentRepository paymentRepository) {
        this.eventRepository = eventRepository;
        this.eventSeatRepository = eventSeatRepository;
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request, User user) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + request.getEventId()));

        List<Long> requestedSeatIds = request.getEventSeatIds();
        if (requestedSeatIds == null || requestedSeatIds.isEmpty()) {
            throw new BadRequestException("Select at least one seat");
        }

        List<EventSeat> selectedSeats = eventSeatRepository.findAllForUpdate(event.getId(), requestedSeatIds);
        if (selectedSeats.size() != requestedSeatIds.size()) {
            throw new BadRequestException("One or more seats do not belong to the selected event");
        }

        for (EventSeat seat : selectedSeats) {
            if (!seat.getEvent().getId().equals(event.getId())) {
                throw new BadRequestException("Seat belongs to another event");
            }
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new BadRequestException("Seat is already reserved: " + seat.getSeat().getRowLabel() + seat.getSeat().getSeatNumber());
            }
        }

        double totalAmount = selectedSeats.stream().mapToDouble(EventSeat::getPrice).sum();

        Booking booking = Booking.builder()
                .user(user)
                .event(event)
                .totalAmount(totalAmount)
                .status(BookingStatus.CONFIRMED)
                .build();
        Booking savedBooking = bookingRepository.save(booking);

        List<String> ticketNumbers = new ArrayList<>();
        for (EventSeat seat : selectedSeats) {
            seat.setStatus(SeatStatus.BOOKED);
            eventSeatRepository.save(seat);

            String ticketNumber = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Ticket ticket = Ticket.builder()
                    .booking(savedBooking)
                    .eventSeat(seat)
                    .ticketNumber(ticketNumber)
                    .price(seat.getPrice())
                    .status(TicketStatus.VALID)
                    .build();
            ticketRepository.save(ticket);
            savedBooking.getTickets().add(ticket);
            ticketNumbers.add(ticketNumber);
        }

        Payment payment = Payment.builder()
                .booking(savedBooking)
                .amount(totalAmount)
                .paymentMethod("SIMULATED")
                .referenceCode("PAY-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase())
                .status(PaymentStatus.PAID)
                .build();
        Payment savedPayment = paymentRepository.save(payment);
        savedBooking.setPayment(savedPayment);

        return BookingResponse.builder()
                .bookingId(savedBooking.getId())
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .userId(user.getId())
                .totalAmount(totalAmount)
                .status(savedBooking.getStatus().name())
                .createdAt(savedBooking.getCreatedAt())
                .ticketNumbers(ticketNumbers)
                .paymentReference(savedPayment.getReferenceCode())
                .build();
    }

    public List<Booking> findByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public Booking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
    }

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Transactional
    public Booking cancelBooking(Long bookingId, User user) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You can only cancel your own bookings");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        for (Ticket ticket : booking.getTickets()) {
            ticket.setStatus(TicketStatus.CANCELLED);
            ticketRepository.save(ticket);
            EventSeat seat = ticket.getEventSeat();
            seat.setStatus(SeatStatus.AVAILABLE);
            eventSeatRepository.save(seat);
        }
        return bookingRepository.save(booking);
    }
}
