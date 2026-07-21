package com.fx.rates;

import java.math.BigDecimal;
import java.time.LocalDate;

/** One point in a pair's rate history. Matches the `/history` endpoint's contract. */
public record RateHistoryEntry(BigDecimal rate, LocalDate rateDate) {
}
