package com.fx.transfer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * GET  /api/transfers -> 200 + every recorded transfer, newest first.
 * POST /api/transfers -> 201 + the recorded transfer (the write half — copy of
 *                         com.fx.sample.CurrencyController's POST /api/currencies pattern).
 */
@RestController
public class TransferController {

    private final TransferRepository repo;

    public TransferController(TransferRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/api/transfers")
    public List<Transfer> all() {
        return repo.findAllNewestFirst();
    }

    @PostMapping("/api/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    public Transfer record(@RequestBody Transfer body) {
        if (body == null || body.fromAccount() == null || body.toAccount() == null
                || body.amount() == null || body.amount().compareTo(BigDecimal.ZERO) <= 0
                || body.currency() == null || body.currency().isBlank()) {
            throw new IllegalArgumentException("fromAccount, toAccount, a positive amount and currency are required");
        }
        String status = (body.status() == null || body.status().isBlank()) ? "COMPLETED" : body.status();
        Transfer toSave = new Transfer(null, body.fromAccount(), body.toAccount(), body.amount(),
                body.currency().toUpperCase(), LocalDateTime.now(), status);
        repo.add(toSave);
        return toSave;
    }
}
