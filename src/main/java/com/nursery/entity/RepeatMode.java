package com.nursery.entity;

/** How often a task recurs. EVERY_N uses the task's {@code everyN} interval. */
public enum RepeatMode {
    DAILY,
    EVERY_N,
    WEEKLY,
    ONCE
}
