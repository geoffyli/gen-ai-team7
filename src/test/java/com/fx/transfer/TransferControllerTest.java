package com.fx.transfer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-slice tests for the Transfers endpoints (mock the repo — no database).
 * Copy of com.fx.sample.CurrencyControllerTest's write-test pattern.
 */
@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    TransferRepository repo;

    @Test
    void recordingATransferCallsTheInsert() throws Exception {
        when(repo.add(any(Transfer.class))).thenReturn(1);

        mvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromAccount\":1,\"toAccount\":2,\"amount\":100.00,\"currency\":\"USD\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fromAccount").value(1))
                .andExpect(jsonPath("$.toAccount").value(2))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(repo).add(any(Transfer.class));
    }

    @Test
    void rejectsNonPositiveAmountWith400() throws Exception {
        mvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromAccount\":1,\"toAccount\":2,\"amount\":0,\"currency\":\"USD\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void listsTransfersNewestFirst() throws Exception {
        LocalDateTime newer = LocalDateTime.of(2026, 1, 12, 14, 0);
        LocalDateTime older = LocalDateTime.of(2026, 1, 10, 9, 0);
        when(repo.findAllNewestFirst()).thenReturn(List.of(
                new Transfer(2, 1, 2, new BigDecimal("50.00"), "USD", newer, "COMPLETED"),
                new Transfer(1, 3, 4, new BigDecimal("20.00"), "EUR", older, "COMPLETED")));

        mvc.perform(get("/api/transfers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[1].id").value(1));
    }
}
