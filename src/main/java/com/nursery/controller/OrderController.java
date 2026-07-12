package com.nursery.controller;

import com.nursery.dto.BatchResponse;
import com.nursery.dto.CreateOrderRequest;
import com.nursery.dto.OrderResponse;
import com.nursery.dto.SeedRequest;
import com.nursery.dto.UpdateOrderRequest;
import com.nursery.service.BatchService;
import com.nursery.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final BatchService batchService;

    public OrderController(OrderService orderService, BatchService batchService) {
        this.orderService = orderService;
        this.batchService = batchService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest req) {
        return orderService.create(req);
    }

    @GetMapping
    public List<OrderResponse> list() {
        return orderService.list();
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable UUID id) {
        return orderService.get(id);
    }

    @PutMapping("/{id}")
    public OrderResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateOrderRequest req) {
        return orderService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        orderService.delete(id);
    }

    @PostMapping("/{id}/seed")
    @ResponseStatus(HttpStatus.CREATED)
    public BatchResponse seed(@PathVariable UUID id, @Valid @RequestBody SeedRequest req) {
        return orderService.seed(id, req);
    }

    @GetMapping("/{id}/batches")
    public List<BatchResponse> batches(@PathVariable UUID id) {
        return batchService.listByOrder(id);
    }
}
