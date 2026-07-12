package com.nursery.dto;

import com.nursery.entity.PriceMode;
import com.nursery.entity.SeedSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** id is optional: supplied by the (offline) client when present, otherwise generated server-side. */
public record CreateOrderRequest(
        UUID id,
        @NotBlank String farmer,
        String phone,
        @NotBlank String crop,
        String variety,
        @Positive int qty,
        LocalDate seededDate,
        @NotNull SeedSource seedSource,
        @NotNull PriceMode priceMode,
        @NotNull @PositiveOrZero BigDecimal rate
) {
}
