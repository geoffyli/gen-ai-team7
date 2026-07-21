package com.fx.rates;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * GET /api/rates              -> 200 + every pair's latest rate.
 * GET /api/rates/{base}/{quote} -> 200 + one pair's latest rate, or 404 if unknown.
 * GET /api/rates/{base}/{quote}/history -> 200 + every history row for that pair, oldest -> newest
 *   (200 + [] for an unknown pair — an empty history is not an error).
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

    @GetMapping("/api/rates/{base}/{quote}/history")
    public List<RateHistoryEntry> history(@PathVariable String base, @PathVariable String quote) {
        String b = base.toUpperCase();
        String q = quote.toUpperCase();
        return repo.findHistory(b, q).stream()
                .map(r -> new RateHistoryEntry(r.rate(), r.rateDate()))
                .collect(Collectors.toList());
    }
}
