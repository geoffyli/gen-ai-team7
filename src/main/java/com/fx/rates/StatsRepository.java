package com.fx.rates;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Aggregates seeded transfer/fx_rate activity for GET /api/stats.
 */
@Repository
public class StatsRepository {

    private final JdbcTemplate jdbc;

    public StatsRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public StatsResponse readStats() {
        return jdbc.queryForObject("""
                SELECT
                    (SELECT COUNT(*) FROM transfer) AS total_transfers,
                    (
                        SELECT currency_code
                        FROM transfer
                        GROUP BY currency_code
                        ORDER BY COUNT(*) DESC, currency_code ASC
                        LIMIT 1
                    ) AS busiest_currency,
                    (SELECT MAX(rate_date) FROM fx_rate) AS latest_rate_date
                """,
                (rs, rowNum) -> {
                    var latest = rs.getDate("latest_rate_date");
                    return new StatsResponse(
                            rs.getLong("total_transfers"),
                            rs.getString("busiest_currency"),
                            latest == null ? null : latest.toLocalDate());
                });
    }
}