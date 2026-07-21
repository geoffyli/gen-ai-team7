package com.fx.sample;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SAMPLE feature — data access with Spring's JdbcTemplate over the seeded `currency` table.
 * The RowMapper turns one DB row into one Currency. Constructor injection gives us the
 * JdbcTemplate that Spring Boot auto-configured from application.properties / the compose env.
 */
@Repository
public class CurrencyRepository {

    private final JdbcTemplate jdbc;

    private static final RowMapper<Currency> MAPPER = (rs, rowNum) -> new Currency(
            rs.getString("code"),
            rs.getString("name"),
            rs.getString("symbol"));

    public CurrencyRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Currency> findAll() {
        return jdbc.query("SELECT code, name, symbol FROM currency ORDER BY code", MAPPER);
    }

    /**
     * SAMPLE write path — the INSERT half. This is the pattern to copy for features that
     * write (e.g. recording a transfer): jdbc.update(...) with '?' placeholders, never string
     * concatenation. Returns the number of rows affected (1 on success).
     */
    public int add(Currency c) {
        return jdbc.update("INSERT INTO currency (code, name, symbol) VALUES (?, ?, ?)",
                c.code(), c.name(), c.symbol());
    }
}
