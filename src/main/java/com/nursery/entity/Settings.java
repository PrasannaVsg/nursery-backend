package com.nursery.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Owner-set configuration for a nursery: total tray capacity, per-crop lead times
 * (crop -> days, used to compute ready dates) and language.
 */
@Entity
@Table(name = "settings")
@Getter
@Setter
public class Settings extends BaseEntity {

    @Column(name = "tray_total", nullable = false)
    private int trayTotal;

    private String language;

    /** Crop -> lead time in days (chilli ~40, tomato ~24, ...). Editable. Small config map: fetch eagerly. */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "settings_crop_lead_times", joinColumns = @JoinColumn(name = "settings_id"))
    @MapKeyColumn(name = "crop")
    @Column(name = "days")
    private Map<String, Integer> cropLeadTimes = new HashMap<>();
}
