package com.fx.sample;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * CONTRACT TEST (a unit test → runs on `./mvnw test`, no Docker).
 *
 * A "contract" test pins the exact SHAPE of an endpoint's JSON — the fields and their types —
 * so the front end and back end can't drift apart. If someone renames `rateDate` to `date`, or
 * drops `symbol`, this fails loudly. Lightweight, framework-free version of contract testing.
 *
 * Given pattern — copy it to pin the response shape of each endpoint you add.
 */
@WebMvcTest(CurrencyController.class)
class CurrencyContractTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    CurrencyRepository repo;

    @Test
    void currenciesEndpointHonoursItsContract() throws Exception {
        when(repo.findAll()).thenReturn(List.of(new Currency("EUR", "Euro", "€")));

        mvc.perform(get("/api/currencies"))
                .andExpect(status().isOk())
                // exactly one object, with exactly these three fields, correct types
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code").isString())
                .andExpect(jsonPath("$[0].name").isString())
                .andExpect(jsonPath("$[0].symbol").isString())
                .andExpect(jsonPath("$[0].code").value("EUR"));
    }
}
