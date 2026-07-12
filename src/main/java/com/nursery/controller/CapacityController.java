package com.nursery.controller;

import com.nursery.dto.CapacityResponse;
import com.nursery.service.TrayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CapacityController {

    private final TrayService trayService;

    public CapacityController(TrayService trayService) {
        this.trayService = trayService;
    }

    @GetMapping("/capacity")
    public CapacityResponse capacity() {
        int total = trayService.trayTotal();
        int reserved = trayService.reservedTrays();
        int occupied = trayService.occupiedTrays();
        int used = reserved + occupied;
        return new CapacityResponse(total, reserved, occupied, used, total - used);
    }
}
