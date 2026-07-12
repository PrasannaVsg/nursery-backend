package com.nursery.dto;

import jakarta.validation.constraints.Positive;

import java.util.Map;

public record UpdateSettingsRequest(
        @Positive int trayTotal,
        String language,
        Map<String, Integer> cropLeadTimes
) {
}
