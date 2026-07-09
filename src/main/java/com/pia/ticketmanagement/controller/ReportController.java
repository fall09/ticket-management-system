package com.pia.ticketmanagement.controller;

import com.pia.ticketmanagement.dto.response.TicketSummaryResponse;

import com.pia.ticketmanagement.dto.response.TopItemResponse;
import com.pia.ticketmanagement.service.TicketService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController

@RequestMapping("/api/reports")

@RequiredArgsConstructor

@CrossOrigin(origins = "http://localhost:5173")

public class ReportController {

    private final TicketService ticketService;

    @GetMapping("/ticket-summary")

    public TicketSummaryResponse getTicketSummary() {

        return ticketService.getTicketSummary();

    }

    @GetMapping("/top-category")
    public TopItemResponse getTopCategory() {
        return ticketService.getTopCategory();
    }

    @GetMapping("/top-province")
    public TopItemResponse getTopProvince() {
        return ticketService.getTopProvince();
    }

    @GetMapping("/top-priority")
    public TopItemResponse getTopPriority() {
        return ticketService.getTopPriority();
    }
    @GetMapping("/top-sub-category")
    public TopItemResponse getTopSubCategory() {
        return ticketService.getTopSubCategory();
    }
    @GetMapping("/category-distribution")
    public List<Object[]> getCategoryDistribution() {
        return ticketService.getCategoryDistribution();
    }

    @GetMapping("/priority-distribution")
    public List<Object[]> getPriorityDistribution() {
        return ticketService.getPriorityDistribution();
    }
    @GetMapping("/province-distribution")
    public List<Object[]> getProvinceDistribution() {
        return ticketService.getProvinceDistribution();
    }

    @GetMapping("/daily-trend")
    public List<Map<String, Object>> getDailyTrend() {
        return ticketService.getDailyTrend();
    }

}