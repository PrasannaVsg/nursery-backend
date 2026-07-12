package com.nursery.dto;

import jakarta.validation.constraints.Positive;

import java.util.UUID;

/** Partial takeout. qty must not exceed the batch's remaining plants. */
public record DispatchRequest(
        @Positive int qty,
        UUID workerId,
        String note
) {
}
