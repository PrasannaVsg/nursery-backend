package com.nursery.dto;

import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

/** Record a mortality count: the new alive total. Must not drop below what's already dispatched. */
public record CountRequest(
        @PositiveOrZero int alive,
        UUID workerId,
        String note
) {
}
