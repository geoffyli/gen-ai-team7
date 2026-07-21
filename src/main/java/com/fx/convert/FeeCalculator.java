package com.fx.convert;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * The retail fee schedule (checkpoints — never invent numbers):
 * amount &lt; 1000 -> 1.0% · 1000-9999 -> 0.5% · &gt;= 10000 -> 0.25% · minimum fee 1.00.
 */
public final class FeeCalculator {

    private static final BigDecimal TIER_LOW = new BigDecimal("1000");
    private static final BigDecimal TIER_HIGH = new BigDecimal("10000");
    private static final BigDecimal RATE_LOW = new BigDecimal("0.010");
    private static final BigDecimal RATE_MID = new BigDecimal("0.005");
    private static final BigDecimal RATE_HIGH = new BigDecimal("0.0025");
    private static final BigDecimal MIN_FEE = new BigDecimal("1.00");

    private FeeCalculator() {
    }

    public static BigDecimal feeFor(BigDecimal amount) {
        BigDecimal tierRate;
        if (amount.compareTo(TIER_LOW) < 0) {
            tierRate = RATE_LOW;
        } else if (amount.compareTo(TIER_HIGH) < 0) {
            tierRate = RATE_MID;
        } else {
            tierRate = RATE_HIGH;
        }
        BigDecimal fee = amount.multiply(tierRate).setScale(2, RoundingMode.HALF_UP);
        return fee.max(MIN_FEE);
    }
}
