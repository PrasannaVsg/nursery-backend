package com.nursery.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * A physical sowing run (a set of trays sown on one date) under an {@link Order}.
 * Holds production data only. crop/variety/seedSource are snapshotted here and locked once sown.
 * <p>Computed values are NOT stored: currentDay (today - sowDate), stage, readyDate.
 */
@Entity
@Table(name = "batches")
@Getter
@Setter
public class Batch extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private String crop;

    private String variety;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeedSource seedSource;

    /** The three counts: ordered (portion of the order assigned here) -> sown -> alive. */
    @Column(nullable = false)
    private int ordered;

    @Column(nullable = false)
    private int sown;

    @Column(nullable = false)
    private int alive;

    @Column(name = "trays_num", nullable = false)
    private int traysNum;

    @Column(name = "sow_date", nullable = false)
    private LocalDate sowDate;

    @Column(nullable = false)
    private boolean closed = false;
}
