package com.nursery.controller;

import com.nursery.dto.BatchResponse;
import com.nursery.dto.CountRequest;
import com.nursery.dto.DispatchRequest;
import com.nursery.service.BatchService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/batches")
public class BatchController {

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    @GetMapping("/{id}")
    public BatchResponse get(@PathVariable UUID id) {
        return batchService.get(id);
    }

    @PostMapping("/{id}/count")
    public BatchResponse count(@PathVariable UUID id, @Valid @RequestBody CountRequest req) {
        return batchService.count(id, req);
    }

    @PostMapping("/{id}/dispatch")
    public BatchResponse dispatch(@PathVariable UUID id, @Valid @RequestBody DispatchRequest req) {
        return batchService.dispatch(id, req);
    }

    @PostMapping("/{id}/close")
    public BatchResponse close(@PathVariable UUID id) {
        return batchService.close(id);
    }
}
