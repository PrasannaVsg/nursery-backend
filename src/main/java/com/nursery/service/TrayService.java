package com.nursery.service;

import com.nursery.entity.Batch;
import com.nursery.entity.Order;
import com.nursery.entity.TimelineEventType;
import com.nursery.exception.BusinessRuleException;
import com.nursery.repository.BatchRepository;
import com.nursery.repository.OrderRepository;
import com.nursery.repository.TimelineEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Central tray-capacity math. Everything here is <b>computed</b> from stored inputs;
 * no reserved/occupied/free figure is ever persisted.
 *
 * <ul>
 *   <li><b>reserved</b> — soft hold on the unseeded remainder of every open order.</li>
 *   <li><b>occupied</b> — hard use by sown batches, shrinking as plants are dispatched.</li>
 *   <li><b>free</b> — total − reserved − occupied.</li>
 * </ul>
 */
@Service
public class TrayService {

    private final OrderRepository orderRepo;
    private final BatchRepository batchRepo;
    private final TimelineEventRepository timelineRepo;
    private final SettingsService settingsService;

    public TrayService(OrderRepository orderRepo, BatchRepository batchRepo,
                       TimelineEventRepository timelineRepo, SettingsService settingsService) {
        this.orderRepo = orderRepo;
        this.batchRepo = batchRepo;
        this.timelineRepo = timelineRepo;
        this.settingsService = settingsService;
    }

    /** Ordered portion already seeded for an order (sum of its batches' ordered counts). */
    public int seededQty(UUID orderId) {
        return batchRepo.sumOrdered(orderId);
    }

    public int dispatched(UUID batchId) {
        return timelineRepo.sumQtyByType(batchId, TimelineEventType.DISPATCH);
    }

    public int remaining(Batch b) {
        return b.getAlive() - dispatched(b.getId());
    }

    @Transactional(readOnly = true)
    public int reservedTrays() {
        int sum = 0;
        for (Order o : orderRepo.findByDeletedFalseOrderByUpdatedAtDesc()) {
            if (o.isClosed()) {
                continue;
            }
            int unseeded = o.getQty() - seededQty(o.getId());
            if (unseeded > 0) {
                sum += NurseryMath.estTrays(unseeded);
            }
        }
        return sum;
    }

    @Transactional(readOnly = true)
    public int occupiedTrays() {
        int sum = 0;
        for (Batch b : batchRepo.findByDeletedFalse()) {
            sum += occupiedTrays(b);
        }
        return sum;
    }

    public int occupiedTrays(Batch b) {
        return NurseryMath.occupiedTrays(b.isClosed(), b.getTraysNum(), b.getAlive(), remaining(b));
    }

    public int trayTotal() {
        return settingsService.trayTotal();
    }

    public int freeTrays() {
        return trayTotal() - reservedTrays() - occupiedTrays();
    }

    /**
     * Enforces the over-capacity block. Call after applying a change inside a transaction;
     * on violation this throws and the transaction rolls back.
     */
    @Transactional(readOnly = true)
    public void assertWithinCapacity() {
        int total = trayTotal();
        int used = reservedTrays() + occupiedTrays();
        if (used > total) {
            throw new BusinessRuleException(
                    "Over tray capacity: needs " + used + " trays but only " + total
                            + " available (free would be " + (total - used) + ").");
        }
    }
}
