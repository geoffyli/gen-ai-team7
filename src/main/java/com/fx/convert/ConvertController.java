package com.fx.convert;

import com.fx.rates.Rate;
import com.fx.rates.RateRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;

/**
 * GET /api/convert?base=EUR&quote=USD&amount=100
 * -> { amount, rate, converted, fee, total } using the latest rate for the pair.
 * `fee` is charged in the quote currency and deducted from `converted` to give `total`.
 */
@RestController
public class ConvertController {

    private final RateRepository rateRepo;

    public ConvertController(RateRepository rateRepo) {
        this.rateRepo = rateRepo;
    }

    @GetMapping("/api/convert")
    public ConversionResult convert(@RequestParam String base,
                                     @RequestParam String quote,
                                     @RequestParam BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than 0");
        }
        String b = base.toUpperCase();
        String q = quote.toUpperCase();
        Rate latest = rateRepo.findLatest(b, q)
                .orElseThrow(() -> new NoSuchElementException("No rate found for " + b + "/" + q));

        BigDecimal converted = amount.multiply(latest.rate()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal fee = FeeCalculator.feeFor(amount);
        BigDecimal total = converted.subtract(fee).setScale(2, RoundingMode.HALF_UP);

        return new ConversionResult(amount, latest.rate(), converted, fee, total);
    }
}
