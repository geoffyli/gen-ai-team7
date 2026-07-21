package com.fx.sample;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * INTEGRATION TEST (naming ends in `IT` → runs on `./mvnw verify`, needs Docker).
 *
 * This is the "real database" test: Testcontainers boots an actual MySQL 8, runs the REAL
 * seed (ops/fxdb-seed.sql), points the app's datasource at it, and exercises the repository's
 * ACTUAL SQL. It catches what mocked unit tests never can — a broken query, a wrong column
 * name, or a charset bug (note it asserts the € symbol imported correctly).
 *
 * You don't need to understand this yet — it's a given pattern. Copy it when you want to test
 * a repository/query for real (e.g. rates "latest per pair", or a transfer INSERT).
 *
 * `disabledWithoutDocker = true`: if Docker isn't reachable on this machine, this test is
 * SKIPPED (not failed) — so `./mvnw verify` still succeeds locally. CI always has Docker, so
 * it runs there for real and still gates the PR. (To run it locally, start Docker Desktop.)
 */
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class CurrencyRepositoryIT {

    @Container
    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8")
            .withDatabaseName("fxdb")
            // run the real seed on first start, exactly like docker-compose does
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
    CurrencyRepository repo;

    @Test
    void loadsTheEightSeededCurrenciesFromARealDb() {
        assertThat(repo.findAll()).hasSize(8);
    }

    @Test
    void symbolsSurviveTheRealImport() {
        assertThat(repo.findAll())
                .anyMatch(c -> c.code().equals("EUR") && "€".equals(c.symbol()));
    }
}
