package com.fx.rates;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data access over the seeded `fx_rate` table, which holds one row per pair per date
 * (history). "Latest" means the row with the max rate_date for that pair.
 */
@Repository
public class RateRepository {

    private final JdbcTemplate jdbc;

    private static final RowMapper<Rate> MAPPER = (rs, rowNum) -> new Rate(
            rs.getString("base_code"),
            rs.getString("quote_code"),
            rs.getBigDecimal("rate"),
            rs.getDate("rate_date").toLocalDate());

    public RateRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Exactly one row per pair — the most recent rate_date for each (base, quote). */
    public List<Rate> findLatestPerPair() {
        return jdbc.query("""
                SELECT r.base_code, r.quote_code, r.rate, r.rate_date
                FROM fx_rate r
                INNER JOIN (
                    SELECT base_code, quote_code, MAX(rate_date) AS max_date
                    FROM fx_rate
                    GROUP BY base_code, quote_code
                ) latest
                ON r.base_code = latest.base_code
                    AND r.quote_code = latest.quote_code
                    AND r.rate_date = latest.max_date
                ORDER BY r.base_code, r.quote_code
                """, MAPPER);
    }

    /** The most recent rate for one specific pair, if it exists. */
    public Optional<Rate> findLatest(String base, String quote) {
        List<Rate> rows = jdbc.query(
                "SELECT base_code, quote_code, rate, rate_date FROM fx_rate "
                        + "WHERE base_code = ? AND quote_code = ? ORDER BY rate_date DESC LIMIT 1",
                MAPPER, base, quote);
        return rows.stream().findFirst();
    }
}
