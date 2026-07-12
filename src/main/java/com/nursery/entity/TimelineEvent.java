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
 * A day-wise event on a {@link Batch} timeline (seed, spray, water, count, dispatch, close).
 */
@Entity
@Table(name = "timeline_events")
@Getter
@Setter
public class TimelineEvent extends BaseEntity {

    @Column(name = "batch_id", nullable = false)
    private UUID batchId;

    /** Day relative to sow date (Day 1 = seeded). */
    @Column(name = "day_number", nullable = false)
    private int dayNumber;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimelineEventType type;

    @Column(name = "event_text")
    private String eventText;

    /** Worker who performed the event, if any. */
    @Column(name = "by_worker_id")
    private UUID byWorkerId;

    /** Quantity dispatched (only for DISPATCH events). */
    private Integer qty;
}
