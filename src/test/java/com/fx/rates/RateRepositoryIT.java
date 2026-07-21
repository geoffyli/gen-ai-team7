package com.fx.rates;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * INTEGRATION TEST (`./mvnw verify`, needs Docker) — exercises the real "latest per pair" SQL
 * against a real MySQL seeded from ops/fxdb-seed.sql. Copy of com.fx.sample.CurrencyRepositoryIT.
 * Skipped (not failed) if Docker isn't reachable locally.
 */
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class RateRepositoryIT {

    @Container
    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8")
            .withDatabaseName("fxdb")
            .withCopyFileToContainer(
                    MountableFile.forHostPath("ops/fxdb-seed.sql"),
                    "/docker-entrypoint-initdb.d/01-seed.sql");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    RateRepository repo;

    @Test
    void returnsExactlyOneRowPerPair() {
        // the seed has 10 pairs x 3 dates each = 30 history rows -> 10 latest rows
        assertThat(repo.findLatestPerPair()).hasSize(10);
    }

    @Test
    void eurUsdLatestMatchesTheCheckpoint() {
        assertThat(repo.findLatest("EUR", "USD")).hasValueSatisfying(rate -> {
            assertThat(rate.rate()).isEqualByComparingTo(new BigDecimal("1.0818"));
            assertThat(rate.rateDate()).isEqualTo(LocalDate.of(2026, 1, 12));
        });
    }

    @Test
    void unknownPairIsEmpty() {
        assertThat(repo.findLatest("EUR", "XXX")).isEmpty();
    }

    @Test
    void eurUsdHistoryIsThreeRowsOldestToNewest() {
        var history = repo.findHistory("EUR", "USD");
        assertThat(history).hasSize(3);
        assertThat(history.get(0).rateDate()).isEqualTo(LocalDate.of(2026, 1, 10));
        assertThat(history.get(2).rateDate()).isEqualTo(LocalDate.of(2026, 1, 12));
        assertThat(history.get(2).rate()).isEqualByComparingTo(new BigDecimal("1.0818"));
    }

    @Test
    void unknownPairHistoryIsEmpty() {
        assertThat(repo.findHistory("EUR", "XXX")).isEmpty();
    }
}
