package com.fx.transfer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** One row of the `transfer` table — a recorded, executed money movement. */
public record Transfer(Integer id, Integer fromAccount, Integer toAccount, BigDecimal amount,
                        String currency, LocalDateTime executedAt, String status) {
}
