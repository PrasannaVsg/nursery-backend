package com.nursery.service;

import com.nursery.dto.BatchResponse;
import com.nursery.dto.CreateOrderRequest;
import com.nursery.dto.OrderResponse;
import com.nursery.dto.SeedRequest;
import com.nursery.dto.UpdateOrderRequest;
import com.nursery.entity.Batch;
import com.nursery.entity.Order;
import com.nursery.entity.PriceMode;
import com.nursery.entity.TimelineEvent;
import com.nursery.entity.TimelineEventType;
import com.nursery.exception.BusinessRuleException;
import com.nursery.exception.NotFoundException;
import com.nursery.repository.BatchRepository;
import com.nursery.repository.OrderRepository;
import com.nursery.repository.TimelineEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final BatchRepository batchRepo;
    private final TimelineEventRepository timelineRepo;
    private final TrayService trayService;
    private final BatchService batchService;

    public OrderService(OrderRepository orderRepo, BatchRepository batchRepo,
                        TimelineEventRepository timelineRepo, TrayService trayService,
                        BatchService batchService) {
        this.orderRepo = orderRepo;
        this.batchRepo = batchRepo;
        this.timelineRepo = timelineRepo;
        this.trayService = trayService;
        this.batchService = batchService;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest req) {
        Order o = new Order();
        o.setId(req.id() != null ? req.id() : UUID.randomUUID());
        o.setFarmer(req.farmer());
        o.setPhone(req.phone());
        o.setCrop(req.crop());
        o.setVariety(req.variety());
        o.setQty(req.qty());
        o.setSeededDate(req.seededDate());
        o.setSeedSource(req.seedSource());
        o.setPriceMode(req.priceMode());
        o.setRate(req.rate());
        o.setClosed(false);
        orderRepo.saveAndFlush(o);

        // Booking must not exceed tray capacity (rolls back on violation).
        trayService.assertWithinCapacity();
        return toResponse(o);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> list() {
        return orderRepo.findByDeletedFalseOrderByUpdatedAtDesc().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse get(UUID id) {
        return toResponse(require(id));
    }

    @Transactional
    public OrderResponse update(UUID id, UpdateOrderRequest req) {
        Order o = require(id);
        boolean hasBatches = batchRepo.existsByOrderIdAndDeletedFalse(id);

        if (hasBatches) {
            // Locked once seeded: only farmer/phone (contact) may change.
            boolean lockedFieldChanged =
                    !Objects.equals(o.getCrop(), req.crop())
                            || !Objects.equals(o.getVariety(), req.variety())
                            || o.getQty() != req.qty()
                            || o.getPriceMode() != req.priceMode()
                            || o.getSeedSource() != req.seedSource()
                            || !Objects.equals(o.getSeededDate(), req.seededDate())
                            || o.getRate().compareTo(req.rate()) != 0;
            if (lockedFieldChanged) {
                throw new BusinessRuleException(
                        "Order has batches: crop/variety/qty/price/seed are locked, only contact is editable.");
            }
            o.setFarmer(req.farmer());
            o.setPhone(req.phone());
            return toResponse(orderRepo.save(o));
        }

        o.setFarmer(req.farmer());
        o.setPhone(req.phone());
        o.setCrop(req.crop());
        o.setVariety(req.variety());
        o.setQty(req.qty());
        o.setSeededDate(req.seededDate());
        o.setSeedSource(req.seedSource());
        o.setPriceMode(req.priceMode());
        o.setRate(req.rate());
        orderRepo.saveAndFlush(o);
        trayService.assertWithinCapacity();
        return toResponse(o);
    }

    @Transactional
    public void delete(UUID id) {
        Order o = require(id);
        if (batchRepo.existsByOrderIdAndDeletedFalse(id)) {
            throw new BusinessRuleException("Cannot delete an order that already has batches.");
        }
        o.setDeleted(true);
        orderRepo.save(o);
    }

    /** Seed a portion of the order, creating a batch (partial allowed) with a Day-1 timeline event. */
    @Transactional
    public BatchResponse seed(UUID id, SeedRequest req) {
        Order o = require(id);
        if (o.isClosed()) {
            throw new BusinessRuleException("Cannot seed a closed order.");
        }
        int unseeded = o.getQty() - trayService.seededQty(id);
        if (req.portion() > unseeded) {
            throw new BusinessRuleException(
                    "Seeding " + req.portion() + " exceeds unseeded remainder " + unseeded + ".");
        }

        LocalDate sowDate = req.sowDate() != null ? req.sowDate() : LocalDate.now();
        int sown = NurseryMath.sownWithBuffer(req.portion());

        Batch b = new Batch();
        b.setId(req.batchId() != null ? req.batchId() : UUID.randomUUID());
        b.setOrderId(id);
        b.setCrop(o.getCrop());
        b.setVariety(o.getVariety());
        b.setSeedSource(o.getSeedSource());
        b.setOrdered(req.portion());
        b.setSown(sown);
        b.setAlive(sown);
        b.setTraysNum(NurseryMath.traysForSown(sown));
        b.setSowDate(sowDate);
        b.setClosed(false);
        batchRepo.save(b);

        TimelineEvent seededEvent = new TimelineEvent();
        seededEvent.setId(UUID.randomUUID());
        seededEvent.setBatchId(b.getId());
        seededEvent.setDayNumber(1);
        seededEvent.setEventDate(sowDate);
        seededEvent.setType(TimelineEventType.SEED);
        seededEvent.setEventText(req.note() != null ? req.note()
                : "Seeded " + req.portion() + " (sown " + sown + ")");
        seededEvent.setByWorkerId(req.workerId());
        timelineRepo.save(seededEvent);

        return batchService.get(b.getId());
    }

    private Order require(UUID id) {
        return orderRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));
    }

    private OrderResponse toResponse(Order o) {
        int seededQty = trayService.seededQty(o.getId());
        BigDecimal contractTotal = o.getPriceMode() == PriceMode.TRAY
                ? o.getRate().multiply(BigDecimal.valueOf(NurseryMath.estTrays(o.getQty())))
                : o.getRate().multiply(BigDecimal.valueOf(o.getQty()));

        List<Batch> batches = batchRepo.findByOrderIdAndDeletedFalseOrderBySowDateAsc(o.getId());
        int delivered = batches.stream().mapToInt(b -> trayService.dispatched(b.getId())).sum();

        return new OrderResponse(
                o.getId(), o.getFarmer(), o.getPhone(), o.getCrop(), o.getVariety(), o.getQty(),
                o.getSeededDate(), o.getSeedSource(), o.getPriceMode(), o.getRate(),
                o.getDiscount(), o.getWriteoffAmount(), o.getWriteoffNote(), o.isClosed(), o.getUpdatedAt(),
                contractTotal, seededQty, o.getQty() - seededQty, delivered, batches.size());
    }
}
