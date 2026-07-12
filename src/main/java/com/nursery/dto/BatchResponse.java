package com.nursery.dto;

import com.nursery.entity.SeedSource;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Batch inputs plus computed production figures. stage/readyDate are intentionally absent —
 * time-progression (auto stage) is a later build step.
 */
public record BatchResponse(
        UUID id,
        UUID orderId,
        String crop,
        String variety,
        SeedSource seedSource,
        int ordered,
        int sown,
        int alive,
        int traysNum,
        LocalDate sowDate,
        boolean closed,
        Instant updatedAt,
        // computed
        int currentDay,
        int dispatched,
        int remaining,
        int occupiedTrays,
        List<TimelineEventResponse> timeline
) {
}
