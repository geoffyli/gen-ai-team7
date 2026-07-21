package com.fx.rates;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-slice tests for the Rates endpoints (mock the repo — no database).
 * Copy of the com.fx.sample.CurrencyControllerTest pattern.
 */
@WebMvcTest(RateController.class)
class RateControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    RateRepository repo;

    @Test
    void returnsLatestRatesAsJson() throws Exception {
        when(repo.findLatestPerPair()).thenReturn(List.of(
                new Rate("EUR", "USD", new BigDecimal("1.0818"), LocalDate.of(2026, 1, 12))));

        mvc.perform(get("/api/rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].base").value("EUR"))
                .andExpect(jsonPath("$[0].quote").value("USD"))
                .andExpect(jsonPath("$[0].rate").value(1.0818))
                .andExpect(jsonPath("$[0].rateDate").value("2026-01-12"));
    }

    @Test
    void emptyDbReturnsEmptyArrayNot500() throws Exception {
        when(repo.findLatestPerPair()).thenReturn(List.of());

        mvc.perform(get("/api/rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void looksUpASinglePair() throws Exception {
        when(repo.findLatest("EUR", "USD")).thenReturn(Optional.of(
                new Rate("EUR", "USD", new BigDecimal("1.0818"), LocalDate.of(2026, 1, 12))));

        mvc.perform(get("/api/rates/EUR/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rate").value(1.0818));
    }

    @Test
    void unknownPairReturns404WithJsonError() throws Exception {
        when(repo.findLatest("EUR", "XXX")).thenReturn(Optional.empty());

        mvc.perform(get("/api/rates/EUR/XXX"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void historyReturnsOldestToNewest() throws Exception {
        when(repo.findHistory("EUR", "USD")).thenReturn(List.of(
                new Rate("EUR", "USD", new BigDecimal("1.0812"), LocalDate.of(2026, 1, 10)),
                new Rate("EUR", "USD", new BigDecimal("1.0815"), LocalDate.of(2026, 1, 11)),
                new Rate("EUR", "USD", new BigDecimal("1.0818"), LocalDate.of(2026, 1, 12))));

        mvc.perform(get("/api/rates/EUR/USD/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].rate").value(1.0812))
                .andExpect(jsonPath("$[0].rateDate").value("2026-01-10"))
                .andExpect(jsonPath("$[2].rate").value(1.0818))
                .andExpect(jsonPath("$[2].rateDate").value("2026-01-12"));
    }

    @Test
    void unknownPairHistoryReturns200WithEmptyArray() throws Exception {
        when(repo.findHistory("EUR", "XXX")).thenReturn(List.of());

        mvc.perform(get("/api/rates/EUR/XXX/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
