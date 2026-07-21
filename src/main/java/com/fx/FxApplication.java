package com.fx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * FX App — the running skeleton for the w2d1-a GenAI sprint.
 *
 * What's already here (the plumbing — you don't rebuild it):
 *   - Spring Boot + JDBC wired to the seeded fxdb (MySQL).
 *   - One SAMPLE feature end-to-end: Currencies (DB -> REST -> UI page under a menu).
 *   - A welcome page, an /health check, and a global error handler.
 *
 * What YOU add (per requirements.md — one branch, one PR, one merge each):
 *   Rates, Conversion, Transfer history, ... — copy the Currencies slice as your pattern.
 */
@SpringBootApplication
public class FxApplication {

    public static void main(String[] args) {
        SpringApplication.run(FxApplication.class, args);
    }
}
