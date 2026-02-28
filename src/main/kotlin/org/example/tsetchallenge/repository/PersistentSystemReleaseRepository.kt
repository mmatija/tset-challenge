package org.example.tsetchallenge.repository

import org.example.tsetchallenge.models.ServiceRelease
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.support.TransactionTemplate


@Repository
class PersistentSystemReleaseRepository(val jdbcTemplate: JdbcTemplate, val transactionTemplate: TransactionTemplate) : SystemReleaseRepository {

    override fun getServiceReleases(systemVersion: Int): List<ServiceRelease> {
        return jdbcTemplate.query("SELECT * FROM service_releases") { resultSet, _ ->
            ServiceRelease(resultSet.getString("service_name"), resultSet.getInt("service_version"))
        }
    }

    override fun addNewRelease(changeset: ServiceRelease, systemReleaseVersion: Int) {
        try {
            transactionTemplate.execute {
                jdbcTemplate.update("INSERT INTO service_releases(service_name, service_version) VALUES (?, ?)", changeset.serviceName, changeset.serviceVersion)
                jdbcTemplate.update("INSERT INTO system_release_versions(version) VALUES (?)", systemReleaseVersion)
            }
        } catch (e: DuplicateKeyException) {
            if (e.localizedMessage.contains("service_release")) {
                throw ServiceReleaseAlreadyExistsException("Release version ${changeset.serviceVersion} for service ${changeset.serviceName} already exists")
            } else if (e.localizedMessage.contains("system_release_version")) {
                throw SystemReleaseVersionAlreadyExistsException("System release version $systemReleaseVersion already exists")
            } else {
                throw e
            }
        }
    }

}
