package com.fx.rates;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * GET /api/stats -> 200 + transfer/rate summary values.
 */
@RestController
public class StatsController {

    private final StatsRepository repo;

    public StatsController(StatsRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/api/stats")
    public StatsResponse stats() {
        return repo.readStats();
    }
}