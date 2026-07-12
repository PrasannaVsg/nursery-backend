package com.nursery.dto;

/** Live tray capacity snapshot — all computed. */
public record CapacityResponse(
        int trayTotal,
        int reserved,
        int occupied,
        int used,
        int free
) {
}
