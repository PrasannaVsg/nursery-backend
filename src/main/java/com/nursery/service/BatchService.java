package com.nursery.service;

import com.nursery.dto.BatchResponse;
import com.nursery.dto.CountRequest;
import com.nursery.dto.DispatchRequest;
import com.nursery.dto.TimelineEventResponse;
import com.nursery.entity.Batch;
import com.nursery.entity.TimelineEvent;
import com.nursery.entity.TimelineEventType;
import com.nursery.exception.BusinessRuleException;
import com.nursery.exception.NotFoundException;
import com.nursery.repository.BatchRepository;
import com.nursery.repository.TimelineEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class BatchService {

    private final BatchRepository batchRepo;
    private final TimelineEventRepository timelineRepo;
    private final TrayService trayService;

    public BatchService(BatchRepository batchRepo, TimelineEventRepository timelineRepo, TrayService trayService) {
        this.batchRepo = batchRepo;
        this.timelineRepo = timelineRepo;
        this.trayService = trayService;
    }

    @Transactional(readOnly = true)
    public List<BatchResponse> listByOrder(UUID orderId) {
        return batchRepo.findByOrderIdAndDeletedFalseOrderBySowDateAsc(orderId).stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public BatchResponse get(UUID id) {
        return toResponse(require(id));
    }

    /** Mortality count: set the new alive total. Cannot fall below already-dispatched, nor exceed sown. */
    @Transactional
    public BatchResponse count(UUID id, CountRequest req) {
        Batch b = require(id);
        int dispatched = trayService.dispatched(id);
        if (req.alive() < dispatched) {
            throw new BusinessRuleException(
                    "Alive " + req.alive() + " cannot be below dispatched " + dispatched + ".");
        }
        if (req.alive() > b.getSown()) {
            throw new BusinessRuleException(
                    "Alive " + req.alive() + " cannot exceed sown " + b.getSown() + ".");
        }
        int loss = b.getAlive() - req.alive();
        b.setAlive(req.alive());
        batchRepo.save(b);

        addEvent(b, TimelineEventType.COUNT,
                req.note() != null ? req.note() : "Count: alive=" + req.alive() + " (loss " + loss + ")",
                req.workerId(), null);
        return toResponse(b);
    }

    /** Partial takeout. qty capped at remaining; frees trays proportionally (via the occupied formula). */
    @Transactional
    public BatchResponse dispatch(UUID id, DispatchRequest req) {
        Batch b = require(id);
        if (b.isClosed()) {
            throw new BusinessRuleException("Cannot dispatch from a closed batch.");
        }
        int remaining = trayService.remaining(b);
        if (req.qty() > remaining) {
            throw new BusinessRuleException(
                    "Dispatch " + req.qty() + " exceeds remaining " + remaining + ".");
        }
        addEvent(b, TimelineEventType.DISPATCH,
                req.note() != null ? req.note() : "Dispatched " + req.qty(),
                req.workerId(), req.qty());
        return toResponse(b);
    }

    @Transactional
    public BatchResponse close(UUID id) {
        Batch b = require(id);
        if (b.isClosed()) {
            return toResponse(b);
        }
        b.setClosed(true);
        batchRepo.save(b);
        addEvent(b, TimelineEventType.CLOSE, "Batch closed", null, null);
        return toResponse(b);
    }

    private void addEvent(Batch b, TimelineEventType type, String text, UUID workerId, Integer qty) {
        TimelineEvent e = new TimelineEvent();
        e.setId(UUID.randomUUID());
        e.setBatchId(b.getId());
        e.setDayNumber(currentDay(b.getSowDate()));
        e.setEventDate(LocalDate.now());
        e.setType(type);
        e.setEventText(text);
        e.setByWorkerId(workerId);
        e.setQty(qty);
        timelineRepo.save(e);
    }

    private Batch require(UUID id) {
        return batchRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Batch not found: " + id));
    }

    /** Day 1 = sow day. */
    private static int currentDay(LocalDate sowDate) {
        return (int) (LocalDate.now().toEpochDay() - sowDate.toEpochDay()) + 1;
    }

    private BatchResponse toResponse(Batch b) {
        int dispatched = trayService.dispatched(b.getId());
        int remaining = b.getAlive() - dispatched;
        List<TimelineEventResponse> timeline =
                timelineRepo.findByBatchIdAndDeletedFalseOrderByDayNumberAscUpdatedAtAsc(b.getId()).stream()
                        .map(e -> new TimelineEventResponse(e.getId(), e.getDayNumber(), e.getEventDate(),
                                e.getType(), e.getEventText(), e.getByWorkerId(), e.getQty()))
                        .toList();

        return new BatchResponse(
                b.getId(), b.getOrderId(), b.getCrop(), b.getVariety(), b.getSeedSource(),
                b.getOrdered(), b.getSown(), b.getAlive(), b.getTraysNum(), b.getSowDate(), b.isClosed(),
                b.getUpdatedAt(),
                currentDay(b.getSowDate()), dispatched, remaining, trayService.occupiedTrays(b), timeline);
    }
}
