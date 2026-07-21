package com.fx.rates;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * CONTRACT TEST — pins the exact shape of GET /api/rates.
 * Copy of com.fx.sample.CurrencyContractTest.
 */
@WebMvcTest(RateController.class)
class RateContractTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    RateRepository repo;

    @Test
    void ratesEndpointHonoursItsContract() throws Exception {
        when(repo.findLatestPerPair()).thenReturn(List.of(
                new Rate("EUR", "USD", new BigDecimal("1.0818"), LocalDate.of(2026, 1, 12))));

        mvc.perform(get("/api/rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].base").isString())
                .andExpect(jsonPath("$[0].quote").isString())
                .andExpect(jsonPath("$[0].rate").isNumber())
                .andExpect(jsonPath("$[0].rateDate").isString());
    }
}
