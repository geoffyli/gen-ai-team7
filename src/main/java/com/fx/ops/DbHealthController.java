package com.fx.ops;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DB check + a quick view of what the seed loaded.
 * GET /api/health/db -> {"status":"UP","tables":{"currency":8,"account":20,"fx_rate":30,"transfer":200}}
 * Handy for confirming `docker compose up` seeded fxdb before you start building features.
 */
@RestController
public class DbHealthController {

    private static final List<String> TABLES = List.of("currency", "account", "fx_rate", "transfer");

    private final JdbcTemplate jdbc;

    public DbHealthController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/api/health/db")
    public Map<String, Object> dbHealth() {
        Map<String, Object> out = new LinkedHashMap<>();
        try {
            jdbc.queryForObject("SELECT 1", Integer.class);
            out.put("status", "UP");
            Map<String, Object> counts = new LinkedHashMap<>();
            for (String table : TABLES) {
                try {
                    counts.put(table, jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Long.class));
                } catch (Exception e) {
                    counts.put(table, "missing");
                }
            }
            out.put("tables", counts);
        } catch (Exception e) {
            out.put("status", "DOWN");
            out.put("hint", "Is MySQL up and is fxdb seeded? Try `docker compose up`. " + e.getMessage());
        }
        return out;
    }
}
