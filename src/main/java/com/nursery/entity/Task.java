package com.nursery.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A scheduled job (water/spray/seed/other). Appears only on its due days, computed from
 * repeat + start (never stored). Scope is one or more batches, the whole nursery
 * ({@code nurseryScope}), or an order (seed tasks). Which days are "due" is computed, not stored.
 */
@Entity
@Table(name = "tasks")
@Getter
@Setter
public class Task extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType type;

    /** True when the task applies to the whole nursery rather than specific batches. */
    @Column(name = "nursery_scope", nullable = false)
    private boolean nurseryScope = false;

    /** Set for seed tasks, which target an order rather than batches. */
    @Column(name = "order_id")
    private UUID orderId;

    /** Assigned worker, if any. */
    @Column(name = "worker_id")
    private UUID workerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_mode", nullable = false)
    private RepeatMode repeatMode;

    /** Interval for {@link RepeatMode#EVERY_N}. */
    @Column(name = "every_n")
    private Integer everyN;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private boolean stopped = false;

    /** Batch ids this task targets (multi-batch selection). */
    @ElementCollection
    @CollectionTable(name = "task_batch_scopes", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "batch_id")
    private Set<UUID> batchScopes = new HashSet<>();
}
