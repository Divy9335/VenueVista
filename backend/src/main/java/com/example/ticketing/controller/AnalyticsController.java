package com.example.ticketing.controller;

import com.example.ticketing.dto.ApiResponse;
import com.example.ticketing.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> overview() {
        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder().success(true).message("Overview fetched").data(analyticsService.getOverview()).build());
    }

    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<Map<String, Object>>> revenue() {
        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder().success(true).message("Revenue fetched").data(analyticsService.getRevenue()).build());
    }

    @GetMapping("/ticket-sales")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> ticketSales() {
        return ResponseEntity.ok(ApiResponse.<List<Map<String, Object>>>builder().success(true).message("Ticket sales fetched").data(analyticsService.getTicketSales()).build());
    }

    @GetMapping("/popular-events")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> popularEvents() {
        return ResponseEntity.ok(ApiResponse.<List<Map<String, Object>>>builder().success(true).message("Popular events fetched").data(analyticsService.getPopularEvents()).build());
    }
}
