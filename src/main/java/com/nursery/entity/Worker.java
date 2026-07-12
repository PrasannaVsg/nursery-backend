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
 * A nursery worker. Daily-wage or monthly-salary. Attendance, advances and payslips
 * are separate append-style records referencing this worker.
 */
@Entity
@Table(name = "workers")
@Getter
@Setter
public class Worker extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_type", nullable = false)
    private PayType payType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal rate;

    @Column(name = "join_date")
    private LocalDate joinDate;
}
