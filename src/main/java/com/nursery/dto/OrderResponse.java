package com.nursery.dto;

import com.nursery.entity.PriceMode;
import com.nursery.entity.SeedSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Stored inputs plus computed money/production figures (contractTotal, seededQty, ... are never stored). */
public record OrderResponse(
        UUID id,
        String farmer,
        String phone,
        String crop,
        String variety,
        int qty,
        LocalDate seededDate,
        SeedSource seedSource,
        PriceMode priceMode,
        BigDecimal rate,
        BigDecimal discount,
        BigDecimal writeoffAmount,
        String writeoffNote,
        boolean closed,
        Instant updatedAt,
        // computed
        BigDecimal contractTotal,
        int seededQty,
        int unseededQty,
        int delivered,
        int batchCount
) {
}
