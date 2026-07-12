package com.nursery.service;

import com.nursery.entity.Settings;
import com.nursery.exception.NotFoundException;
import com.nursery.repository.SettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Single-nursery settings for now (one row). Multi-nursery scoping arrives with auth (step 5).
 */
@Service
public class SettingsService {

    private final SettingsRepository repo;

    public SettingsService(SettingsRepository repo) {
        this.repo = repo;
    }

    /** The singleton settings row. */
    @Transactional(readOnly = true)
    public Settings get() {
        List<Settings> all = repo.findAll();
        if (all.isEmpty()) {
            throw new NotFoundException("Settings not initialised");
        }
        return all.get(0);
    }

    public int trayTotal() {
        return get().getTrayTotal();
    }

    @Transactional
    public Settings update(int trayTotal, String language, Map<String, Integer> cropLeadTimes) {
        Settings s = get();
        s.setTrayTotal(trayTotal);
        if (language != null) {
            s.setLanguage(language);
        }
        if (cropLeadTimes != null) {
            s.getCropLeadTimes().clear();
            s.getCropLeadTimes().putAll(cropLeadTimes);
        }
        return repo.save(s);
    }

    /** Bootstraps the singleton row with sensible defaults if none exists yet. */
    @Transactional
    public void ensureInitialised() {
        if (!repo.findAll().isEmpty()) {
            return;
        }
        Settings s = new Settings();
        s.setId(UUID.randomUUID());
        s.setTrayTotal(2000);
        s.setLanguage("te");
        // Default crop lead times in days (editable). Kept for step 7 (ready-date computation).
        s.getCropLeadTimes().put("chilli", 40);
        s.getCropLeadTimes().put("tomato", 24);
        s.getCropLeadTimes().put("brinjal", 35);
        s.getCropLeadTimes().put("capsicum", 50);
        s.getCropLeadTimes().put("cabbage", 30);
        repo.save(s);
    }
}
