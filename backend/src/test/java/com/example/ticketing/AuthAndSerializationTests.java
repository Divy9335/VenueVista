package com.example.ticketing;

import com.example.ticketing.dto.RegisterRequest;
import com.example.ticketing.entity.Event;
import com.example.ticketing.entity.Booking;
import com.example.ticketing.entity.Ticket;
import com.example.ticketing.entity.User;
import com.example.ticketing.entity.Venue;
import com.example.ticketing.enums.Role;
import com.example.ticketing.repository.UserRepository;
import com.example.ticketing.security.JwtService;
import com.example.ticketing.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthAndSerializationTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void registrationAlwaysCreatesCustomerEvenWhenPrivilegedRoleIsRequested() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("New User");
        request.setEmail("new@example.com");
        request.setPassword("password");
        request.setRole(Role.ADMIN);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(any(User.class))).thenReturn("signed-token");

        var response = authService.register(request);

        assertEquals(Role.CUSTOMER.name(), response.getRole());
        assertEquals("signed-token", response.getToken());
    }

    @Test
    void eventJsonDoesNotRecurseThroughVenueOrExposeUserPassword() throws Exception {
        Venue venue = Venue.builder()
                .id(1L)
                .name("Grand Hall")
                .city("Nairobi")
                .address("123 City Road")
                .capacity(10)
                .build();
        Event event = Event.builder()
                .id(2L)
                .title("Jazz Night")
                .venue(venue)
                .build();
        venue.setEvents(java.util.List.of(event));

        ObjectMapper objectMapper = new ObjectMapper();
        String eventJson = objectMapper.writeValueAsString(event);
        assertNotNull(eventJson);
        assertFalse(eventJson.contains("\"events\""));
        assertFalse(eventJson.contains("hibernateLazyInitializer"));

        User user = User.builder()
                .id(3L)
                .fullName("Demo User")
                .email("demo@example.com")
                .password("hashed-password")
                .role(Role.CUSTOMER)
                .build();
        String userJson = objectMapper.writeValueAsString(user);
        assertFalse(userJson.contains("hashed-password"));
    }

    @Test
    void entityBuildersKeepTheirCollectionAndTimestampDefaults() {
        Booking booking = Booking.builder().build();
        Ticket ticket = Ticket.builder().build();

        assertNotNull(booking.getTickets());
        assertNotNull(ticket.getIssuedAt());
    }
}
