package com.fx.convert;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the fee tiers — no Spring context, pure logic.
 * Checkpoints (never invent numbers): <1000 -> 1.0%, 1000-9999 -> 0.5%, >=10000 -> 0.25%,
 * minimum fee 1.00.
 */
class FeeCalculatorTest {

    @Test
    void checkpointFiveThousandIsTwentyFiveDollars() {
        assertThat(FeeCalculator.feeFor(new BigDecimal("5000")))
                .isEqualByComparingTo(new BigDecimal("25.00"));
    }

    @Test
    void checkpointOneHundredIsOneDollarMinFee() {
        assertThat(FeeCalculator.feeFor(new BigDecimal("100")))
                .isEqualByComparingTo(new BigDecimal("1.00"));
    }

    @Test
    void lowTierEdgeJustBelowOneThousand() {
        // 999.99 * 1.0% = 9.9999 -> rounds to 10.00
        assertThat(FeeCalculator.feeFor(new BigDecimal("999.99")))
                .isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    void midTierEdgeAtOneThousand() {
        // 1000 lands in the 1000-9999 tier (0.5%), not the <1000 tier
        assertThat(FeeCalculator.feeFor(new BigDecimal("1000")))
                .isEqualByComparingTo(new BigDecimal("5.00"));
    }

    @Test
    void midTierEdgeJustBelowTenThousand() {
        // 9999.99 * 0.5% = 49.99995 -> rounds to 50.00
        assertThat(FeeCalculator.feeFor(new BigDecimal("9999.99")))
                .isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    void highTierEdgeAtTenThousand() {
        // 10000 lands in the >=10000 tier (0.25%), not the 1000-9999 tier
        assertThat(FeeCalculator.feeFor(new BigDecimal("10000")))
                .isEqualByComparingTo(new BigDecimal("25.00"));
    }

    @Test
    void minFeeFloorAppliesToTinyAmounts() {
        // 1 * 1.0% = 0.01, floored up to the 1.00 minimum
        assertThat(FeeCalculator.feeFor(new BigDecimal("1")))
                .isEqualByComparingTo(new BigDecimal("1.00"));
    }
}
