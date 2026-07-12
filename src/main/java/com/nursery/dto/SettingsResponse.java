package com.nursery.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record SettingsResponse(
        UUID id,
        int trayTotal,
        String language,
        Map<String, Integer> cropLeadTimes,
        Instant updatedAt
) {
}
