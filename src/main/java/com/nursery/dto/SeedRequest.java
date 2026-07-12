package com.nursery.dto;

import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Seed a portion of an order, creating a batch. Partial allowed: portion must not exceed the
 * order's unseeded remainder. batchId optional (client-supplied id). sowDate defaults to today.
 */
public record SeedRequest(
        UUID batchId,
        @Positive int portion,
        UUID workerId,
        LocalDate sowDate,
        String note
) {
}
