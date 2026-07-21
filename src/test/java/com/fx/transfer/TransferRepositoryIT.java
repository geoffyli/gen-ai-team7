package com.fx.transfer;

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
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * INTEGRATION TEST (`./mvnw verify`, needs Docker) — exercises the real transfer INSERT and
 * the newest-first SELECT against a real MySQL seeded from ops/fxdb-seed.sql.
 * Copy of com.fx.sample.CurrencyRepositoryIT. Skipped (not failed) without Docker.
 */
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class TransferRepositoryIT {

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
    TransferRepository repo;

    @Test
    void insertedTransferLandsAndComesBackFirst() {
        int before = repo.findAllNewestFirst().size();

        Transfer t = new Transfer(null, 1, 2, new BigDecimal("42.50"), "USD",
                LocalDateTime.now(), "COMPLETED");
        repo.add(t);

        var all = repo.findAllNewestFirst();
        assertThat(all).hasSize(before + 1);
        assertThat(all.get(0).amount()).isEqualByComparingTo(new BigDecimal("42.50"));
    }

    @Test
    void listIsOrderedNewestFirst() {
        var all = repo.findAllNewestFirst();
        for (int i = 0; i < all.size() - 1; i++) {
            assertThat(all.get(i).executedAt()).isAfterOrEqualTo(all.get(i + 1).executedAt());
        }
    }
}
