package com.nursery.dto;

import com.nursery.entity.PriceMode;
import com.nursery.entity.SeedSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Full replacement of editable fields. Once a batch exists, crop/variety/qty/priceMode/rate/
 * seedSource/seededDate are locked — changing any of them is rejected; only farmer/phone stay editable.
 */
public record UpdateOrderRequest(
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
