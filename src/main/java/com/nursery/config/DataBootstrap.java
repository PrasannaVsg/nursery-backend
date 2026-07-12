package com.nursery.config;

import com.nursery.service.SettingsService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Seeds the singleton settings row on first boot so capacity checks have a tray total. */
@Configuration
public class DataBootstrap {

    @Bean
    ApplicationRunner initSettings(SettingsService settingsService) {
        return args -> settingsService.ensureInitialised();
    }
}
