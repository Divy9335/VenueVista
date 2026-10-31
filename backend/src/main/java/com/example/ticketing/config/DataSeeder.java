package com.example.ticketing.config;

import com.example.ticketing.entity.*;
import com.example.ticketing.enums.*;
import com.example.ticketing.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;
    private final EventSeatRepository eventSeatRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      VenueRepository venueRepository,
                      SeatRepository seatRepository,
                      EventRepository eventRepository,
                      EventSeatRepository eventSeatRepository,
                      BookingRepository bookingRepository,
                      TicketRepository ticketRepository,
                      PaymentRepository paymentRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.venueRepository = venueRepository;
        this.seatRepository = seatRepository;
        this.eventRepository = eventRepository;
        this.eventSeatRepository = eventSeatRepository;
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.paymentRepository = paymentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User admin = userRepository.save(User.builder()
                .fullName("System Administrator")
                .email("admin@ticketing.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .build());

        User organizer = userRepository.save(User.builder()
                .fullName("Event Organizer")
                .email("organizer@ticketing.com")
                .password(passwordEncoder.encode("organizer123"))
                .role(Role.ORGANIZER)
                .build());

        User customer = userRepository.save(User.builder()
                .fullName("Demo Customer")
                .email("customer@ticketing.com")
                .password(passwordEncoder.encode("customer123"))
                .role(Role.CUSTOMER)
                .build());

        Venue venue = venueRepository.save(Venue.builder()
                .name("Grand Hall")
                .city("Nairobi")
                .address("123 City Road")
                .capacity(12)
                .build());

        for (char row = 'A'; row <= 'D'; row++) {
            for (int seatNumber = 1; seatNumber <= 3; seatNumber++) {
                Seat seat = Seat.builder()
                        .venue(venue)
                        .rowLabel(String.valueOf(row))
                        .seatNumber(seatNumber)
                        .price(120.0)
                        .build();
                seatRepository.save(seat);
            }
        }

        Event event = eventRepository.save(Event.builder()
                .title("Sunset Jazz Night")
                .description("Live jazz performance in the city center")
                .startTime(LocalDateTime.now().plusDays(2).withHour(19).withMinute(0))
                .endTime(LocalDateTime.now().plusDays(2).withHour(21).withMinute(30))
                .basePrice(120.0)
                .venue(venue)
                .build());

        for (Seat seat : seatRepository.findByVenueId(venue.getId())) {
            EventSeat eventSeat = EventSeat.builder()
                    .event(event)
                    .seat(seat)
                    .status(SeatStatus.AVAILABLE)
                    .price(event.getBasePrice())
                    .build();
            eventSeatRepository.save(eventSeat);
        }

        Booking booking = bookingRepository.save(Booking.builder()
                .user(customer)
                .event(event)
                .totalAmount(240.0)
                .status(BookingStatus.CONFIRMED)
                .build());

        EventSeat soldSeat = eventSeatRepository.findByEventId(event.getId()).get(0);
        soldSeat.setStatus(SeatStatus.BOOKED);
        eventSeatRepository.save(soldSeat);

        Ticket ticket = ticketRepository.save(Ticket.builder()
                .booking(booking)
                .eventSeat(soldSeat)
                .ticketNumber("TKT-1001")
                .price(soldSeat.getPrice())
                .status(TicketStatus.VALID)
                .build());

        paymentRepository.save(Payment.builder()
                .booking(booking)
                .amount(240.0)
                .paymentMethod("SIMULATED")
                .referenceCode("PAY-1001")
                .status(PaymentStatus.PAID)
                .build());

        booking.setTickets(java.util.List.of(ticket));
        bookingRepository.save(booking);
    }
}
