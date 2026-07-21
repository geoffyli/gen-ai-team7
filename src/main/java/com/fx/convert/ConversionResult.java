package com.fx.convert;

import java.math.BigDecimal;

/** The result of converting `amount` of `base` into `quote` at the latest rate. */
public record ConversionResult(BigDecimal amount, BigDecimal rate, BigDecimal converted,
                                BigDecimal fee, BigDecimal total) {
}
