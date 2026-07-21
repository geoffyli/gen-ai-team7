package com.fx.sample;

/**
 * SAMPLE feature — the reference "currency" entity, read from the fxdb `currency` table.
 * This whole `com.fx.sample` package is your worked example of a full vertical slice:
 * a model (this record) -> a repository (JDBC) -> a controller (REST) -> a UI page.
 * Copy this shape for your own features (Rates, Conversion, ...). You may delete `sample`
 * once you've built a couple of real features and no longer need the reference.
 */
public record Currency(String code, String name, String symbol) {
}
