package com.example.ticketing.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class BookingRequest {
    @NotNull
    private Long eventId;

    @NotEmpty
    private List<Long> eventSeatIds;
}
