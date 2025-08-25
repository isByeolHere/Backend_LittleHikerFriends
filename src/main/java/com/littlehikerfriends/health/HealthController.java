package com.littlehikerfriends.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "ok");
        body.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(body);
    }
    
    // 루트 경로 매핑 추가
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Little Hiker Friends API");
        body.put("status", "running");
        body.put("version", "1.0.0");
        body.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(body);
    }
}


