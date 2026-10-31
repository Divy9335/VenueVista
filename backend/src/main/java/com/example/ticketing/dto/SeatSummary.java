package com.example.ticketing.dto;

import com.example.ticketing.enums.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatSummary {
    private Long seatId;
    private Long eventSeatId;
    private String rowLabel;
    private Integer seatNumber;
    private Double price;
    private SeatStatus status;
}
