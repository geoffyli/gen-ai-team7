package com.fx.sample;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * SAMPLE feature — the REST endpoints the Currencies UI page calls.
 *
 * GET  /api/currencies  ->  200 + JSON array (the READ pattern).
 * POST /api/currencies  ->  201 + the created object (the WRITE pattern) — copy this shape
 *                            for features that create rows, e.g. recording a transfer.
 *
 * Thin controller: it validates input, delegates to the repository, and lets
 * com.fx.web.ApiExceptionHandler turn a bad request into a clean 400 (no stack trace).
 */
@RestController
public class CurrencyController {

    private final CurrencyRepository repo;

    public CurrencyController(CurrencyRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/api/currencies")
    public List<Currency> all() {
        return repo.findAll();
    }

    @PostMapping("/api/currencies")
    @ResponseStatus(HttpStatus.CREATED)
    public Currency add(@RequestBody Currency c) {
        if (c == null || c.code() == null || c.code().isBlank()
                || c.name() == null || c.name().isBlank()) {
            // -> 400 via ApiExceptionHandler; a customer never sees a stack trace.
            throw new IllegalArgumentException("code and name are required");
        }
        repo.add(c);
        return c;
    }
}
