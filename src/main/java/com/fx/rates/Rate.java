package com.fx.rates;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * The latest known rate for one currency pair.
 * Mirrors the sample's Currency record — see com.fx.sample for the pattern.
 */
public record Rate(String base, String quote, BigDecimal rate, LocalDate rateDate) {
}
