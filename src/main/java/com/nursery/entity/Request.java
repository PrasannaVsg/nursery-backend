package com.nursery.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * An incoming farmer enquiry (pre-order). Accepting it creates an {@link Order}; rejecting dismisses it.
 */
@Entity
@Table(name = "requests")
@Getter
@Setter
public class Request extends BaseEntity {

    @Column(nullable = false)
    private String farmer;

    private String phone;

    @Column(nullable = false)
    private String crop;

    private String variety;

    @Column(nullable = false)
    private int qty;

    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;
}
