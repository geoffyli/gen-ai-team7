package com.fx.convert;

import com.fx.rates.Rate;
import com.fx.rates.RateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Web-slice tests for GET /api/convert (mock the rate repo — no database). */
@WebMvcTest(ConvertController.class)
class ConvertControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    RateRepository rateRepo;

    @Test
    void convertsUsingTheLatestRate() throws Exception {
        when(rateRepo.findLatest("EUR", "USD")).thenReturn(Optional.of(
                new Rate("EUR", "USD", new BigDecimal("1.0818"), LocalDate.of(2026, 1, 12))));

        mvc.perform(get("/api/convert").param("base", "EUR").param("quote", "USD").param("amount", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.converted").value(108.18))
                .andExpect(jsonPath("$.fee").value(1.00));
    }

    @Test
    void nonPositiveAmountReturns400WithNoConversion() throws Exception {
        mvc.perform(get("/api/convert").param("base", "EUR").param("quote", "USD").param("amount", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void unknownPairReturns404() throws Exception {
        when(rateRepo.findLatest("EUR", "XXX")).thenReturn(Optional.empty());

        mvc.perform(get("/api/convert").param("base", "EUR").param("quote", "XXX").param("amount", "100"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    // 05 · validation & error handling — bad input is always a clean 4xx + {error}, never a 500.

    @Test
    void missingAmountParamReturns400WithNoStackTrace() throws Exception {
        mvc.perform(get("/api/convert").param("base", "EUR").param("quote", "USD"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @Test
    void nonNumericAmountReturns400WithNoStackTrace() throws Exception {
        mvc.perform(get("/api/convert").param("base", "EUR").param("quote", "USD").param("amount", "not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.trace").doesNotExist());
    }
}
