package com.fx.sample;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SAMPLE test — web-slice tests for the Currencies endpoints. They mock the repository, so
 * they need NO database and run in CI. Copy this shape (a read test, a write test, a
 * validation test) when you test your own controllers. (@WebMvcTest loads only the web
 * layer + the @RestControllerAdvice, not the DataSource.)
 */
@WebMvcTest(CurrencyController.class)
class CurrencyControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    CurrencyRepository repo;

    @Test
    void returnsCurrenciesAsJson() throws Exception {
        when(repo.findAll()).thenReturn(List.of(
                new Currency("EUR", "Euro", "€"),
                new Currency("USD", "US Dollar", "$")));

        mvc.perform(get("/api/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("EUR"))
                .andExpect(jsonPath("$[1].code").value("USD"));
    }

    @Test
    void createsACurrency() throws Exception {
        when(repo.add(any(Currency.class))).thenReturn(1);

        mvc.perform(post("/api/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"XYZ\",\"name\":\"Testland Dollar\",\"symbol\":\"Z$\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("XYZ"));

        verify(repo).add(any(Currency.class));
    }

    @Test
    void rejectsMissingCodeWith400() throws Exception {
        mvc.perform(post("/api/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"\",\"name\":\"Nameless\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
