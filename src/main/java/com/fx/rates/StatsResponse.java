package com.fx.rates;

import java.time.LocalDate;

/**
 * One-call market activity summary for GET /api/stats.
 */
public record StatsResponse(long totalTransfers, String busiestCurrency, LocalDate latestRateDate) {
}