package com.fx.rates;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-slice test for GET /api/stats (mock repository, no database).
 */
@WebMvcTest(StatsController.class)
class StatsControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    StatsRepository repo;

    @Test
    void returnsSummaryFieldsWithCheckpointValues() throws Exception {
        when(repo.readStats()).thenReturn(new StatsResponse(
                200,
                "USD",
                LocalDate.of(2026, 1, 12)));

        mvc.perform(get("/api/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTransfers").value(200))
                .andExpect(jsonPath("$.busiestCurrency").value("USD"))
                .andExpect(jsonPath("$.latestRateDate").value("2026-01-12"));
    }
}