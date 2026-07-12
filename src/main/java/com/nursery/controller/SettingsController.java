package com.nursery.controller;

import com.nursery.dto.SettingsResponse;
import com.nursery.dto.UpdateSettingsRequest;
import com.nursery.entity.Settings;
import com.nursery.service.SettingsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;

@RestController
@RequestMapping("/settings")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public SettingsResponse get() {
        return toResponse(settingsService.get());
    }

    @PutMapping
    public SettingsResponse update(@Valid @RequestBody UpdateSettingsRequest req) {
        return toResponse(settingsService.update(req.trayTotal(), req.language(), req.cropLeadTimes()));
    }

    private SettingsResponse toResponse(Settings s) {
        return new SettingsResponse(s.getId(), s.getTrayTotal(), s.getLanguage(),
                new LinkedHashMap<>(s.getCropLeadTimes()), s.getUpdatedAt());
    }
}
