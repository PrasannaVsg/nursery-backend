package com.nursery.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * A recorded pay event for a worker: net paid, days counted, advances deducted.
 * A historical fact once created (net is never negative — enforced in the service layer).
 */
@Entity
@Table(name = "payslips")
@Getter
@Setter
public class Payslip extends BaseEntity {

    @Column(name = "worker_id", nullable = false)
    private UUID workerId;

    @Column(name = "payslip_date", nullable = false)
    private LocalDate payslipDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal net;

    @Column(nullable = false)
    private int days;

    @Column(name = "adv_deducted", nullable = false, precision = 12, scale = 2)
    private BigDecimal advDeducted;
}
