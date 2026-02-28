package org.example.tsetchallenge

import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest

class BaseTest() {

    @Autowired
lateinit var jdbcTemplate: JdbcTemplate

    @AfterEach
    fun cleanup() {
        jdbcTemplate.execute("DELETE FROM service_releases")
    }
}