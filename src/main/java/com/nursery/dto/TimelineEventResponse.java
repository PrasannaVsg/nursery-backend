package com.nursery.dto;

import com.nursery.entity.TimelineEventType;

import java.time.LocalDate;
import java.util.UUID;

public record TimelineEventResponse(
        UUID id,
        int dayNumber,
        LocalDate eventDate,
        TimelineEventType type,
        String eventText,
        UUID byWorkerId,
        Integer qty
) {
}
