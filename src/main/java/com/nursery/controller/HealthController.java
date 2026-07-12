package com.nursery.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Step 1 plumbing check. Confirms the app is up and serving HTTP.
 * (Deeper readiness/DB checks come later via Spring Boot Actuator.)
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
