package com.fx.rates;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * GET /api/rates              -> 200 + every pair's latest rate.
 * GET /api/rates/{base}/{quote} -> 200 + one pair's latest rate, or 404 if unknown.
 */
@RestController
public class RateController {

    private final RateRepository repo;

    public RateController(RateRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/api/rates")
    public List<Rate> all() {
        return repo.findLatestPerPair();
    }

    @GetMapping("/api/rates/{base}/{quote}")
    public Rate one(@PathVariable String base, @PathVariable String quote) {
        String b = base.toUpperCase();
        String q = quote.toUpperCase();
        return repo.findLatest(b, q)
                .orElseThrow(() -> new NoSuchElementException("No rate found for " + b + "/" + q));
    }
}
