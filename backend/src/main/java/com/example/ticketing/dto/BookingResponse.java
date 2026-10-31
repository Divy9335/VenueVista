package com.example.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long bookingId;
    private Long eventId;
    private String eventTitle;
    private Long userId;
    private Double totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private List<String> ticketNumbers;
    private String paymentReference;
}
