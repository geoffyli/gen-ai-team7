package com.fx.transfer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * Data access over the seeded `transfer` table.
 * Write pattern copied from com.fx.sample.CurrencyRepository.add().
 */
@Repository
public class TransferRepository {

    private final JdbcTemplate jdbc;

    private static final RowMapper<Transfer> MAPPER = (rs, rowNum) -> new Transfer(
            rs.getInt("id"),
            rs.getInt("from_account"),
            rs.getInt("to_account"),
            rs.getBigDecimal("amount"),
            rs.getString("currency_code"),
            rs.getTimestamp("executed_at").toLocalDateTime(),
            rs.getString("status"));

    public TransferRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Newest first — most recently executed transfer at the top. */
    public List<Transfer> findAllNewestFirst() {
        return jdbc.query(
                "SELECT id, from_account, to_account, amount, currency_code, executed_at, status "
                        + "FROM transfer ORDER BY executed_at DESC, id DESC",
                MAPPER);
    }

    public int add(Transfer t) {
        return jdbc.update(
                "INSERT INTO transfer (from_account, to_account, amount, currency_code, executed_at, status) "
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                t.fromAccount(), t.toAccount(), t.amount(), t.currency(),
                Timestamp.valueOf(t.executedAt()), t.status());
    }
}
