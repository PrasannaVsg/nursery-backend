package com.nursery.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * The farmer's deal. Holds all money (contract inputs, discount, write-off, closed).
 * One order can be split into many {@link Batch}es. Money lives here, never on the batch.
 * <p>Computed values are NOT stored: contract total, balance, seededQty, readyDate.
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity {

    @Column(nullable = false)
    private String farmer;

    private String phone;

    @Column(nullable = false)
    private String crop;

    private String variety;

    @Column(nullable = false)
    private int qty;

    /** Owner enters this; ready date is computed as seededDate + crop lead time. */
    private LocalDate seededDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeedSource seedSource;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriceMode priceMode;

    /** Per-plant or per-tray rate (see {@link #priceMode}). Contract total = qty * rate is computed. */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal rate;

    /** Optional flat discount applied at close. */
    @Column(precision = 12, scale = 2)
    private BigDecimal discount;

    /** Remaining balance can be written off after close (maafi), with a note. */
    @Column(precision = 12, scale = 2)
    private BigDecimal writeoffAmount;

    private String writeoffNote;

    @Column(nullable = false)
    private boolean closed = false;
}
