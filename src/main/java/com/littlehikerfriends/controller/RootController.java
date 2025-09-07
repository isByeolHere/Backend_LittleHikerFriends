package com.littlehikerfriends.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Little Hiker Friends API");
        body.put("status", "running");
        body.put("version", "1.0.0");
        body.put("timestamp", Instant.now().toString());
        body.put("endpoints", Map.of(
            "health", "/api/health/ping",
            "auth", "/api/auth",
            "swagger", "/swagger-ui.html"
        ));
        return ResponseEntity.ok(body);
    }
}
