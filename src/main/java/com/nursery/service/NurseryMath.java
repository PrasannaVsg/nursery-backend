package com.nursery.service;

/**
 * Pure nursery arithmetic, mirrored from the prototype so the backend and UI agree.
 * <ul>
 *   <li>98-cell pro-trays.</li>
 *   <li>~5% germination buffer: you sow a few more than ordered to cover losses.</li>
 * </ul>
 */
public final class NurseryMath {

    public static final int CELLS_PER_TRAY = 98;
    public static final double GERMINATION_BUFFER = 1.05;

    private NurseryMath() {
    }

    /** Trays needed to hold a plant quantity (with buffer), rounded up. Used for reservations. */
    public static int estTrays(int qty) {
        if (qty <= 0) {
            return 0;
        }
        return (int) Math.ceil(qty * GERMINATION_BUFFER / CELLS_PER_TRAY);
    }

    /** Seeds actually sown for an ordered portion = portion + ~5% buffer. */
    public static int sownWithBuffer(int portion) {
        return (int) Math.round(portion * GERMINATION_BUFFER);
    }

    /** Trays a sown count occupies, rounded up. */
    public static int traysForSown(int sown) {
        if (sown <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) sown / CELLS_PER_TRAY);
    }

    /**
     * Trays a batch currently occupies. Shrinks proportionally as plants are dispatched,
     * and is zero once the batch is closed or empty.
     */
    public static int occupiedTrays(boolean closed, int traysNum, int alive, int remaining) {
        if (closed || alive <= 0 || remaining <= 0) {
            return 0;
        }
        return Math.max(0, (int) Math.ceil((double) traysNum * remaining / alive));
    }
}
