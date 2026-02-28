package org.example.tsetchallenge

import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.support.TransactionTemplate

@SpringBootTest

class BaseTest() {

    @Autowired
lateinit var jdbcTemplate: JdbcTemplate
    @Autowired
    lateinit var transactionTemplate: TransactionTemplate

    @AfterEach
    fun cleanup() {
        transactionTemplate.execute {
            jdbcTemplate.execute("DELETE FROM system_releases")
            jdbcTemplate.execute("DELETE FROM service_releases")
            jdbcTemplate.execute("DELETE FROM system_release_versions")
        }
    }
}