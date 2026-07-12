package com.nursery.entity;

/** Payments are append-only. A correction is a new REVERSAL row, never an overwrite. */
public enum PaymentKind {
    ADVANCE,
    PARTIAL,
    PAYMENT,
    REVERSAL
}
