package org.example.tsetchallenge.repository

import org.example.tsetchallenge.models.ServiceRelease
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.support.TransactionTemplate


@Repository
class PersistentSystemReleaseRepository(val jdbcTemplate: JdbcTemplate, val transactionTemplate: TransactionTemplate) :
    SystemReleaseRepository {

    override fun getServiceReleases(systemVersion: Int): List<ServiceRelease> {
        return jdbcTemplate.query(
            "SELECT service_name, service_version FROM system_releases WHERE system_release_version = ?",
            { resultSet, _ ->
                ServiceRelease(resultSet.getString("service_name"), resultSet.getInt("service_version"))
            },
            systemVersion
        )
    }

    override fun createRelease(changeset: ServiceRelease): Int {
        return try {
            transactionTemplate.execute {
                createServiceRelease(changeset)
                incrementSystemReleaseVersion()
                associateServiceReleasesWithLatestSystemReleaseVersion()
                fetchLatestSystemReleaseVersion()
            }
        } catch (e: DuplicateKeyException) {
            if (e.localizedMessage.contains("service_release")) {
                fetchLatestSystemReleaseVersion()
            } else {
                throw e
            }
        }
    }

    private fun associateServiceReleasesWithLatestSystemReleaseVersion() {
        jdbcTemplate.update(
            "INSERT INTO system_releases(service_name, service_version, system_release_version) select distinct on (service_name) service_name, service_version, (select max(version) from system_release_versions) FROM service_releases ORDER BY service_name, service_version DESC"
        )
    }

    private fun incrementSystemReleaseVersion() {
        jdbcTemplate.update("INSERT INTO system_release_versions(version) select coalesce(max(version), 0) + 1 from system_release_versions")
    }

    private fun createServiceRelease(changeset: ServiceRelease) {
        jdbcTemplate.update(
            "INSERT INTO service_releases(service_name, service_version) VALUES (?, ?)",
            changeset.serviceName,
            changeset.serviceVersion
        )
    }

    private fun fetchLatestSystemReleaseVersion(): Int =
        jdbcTemplate.query("SELECT max(version) as max_version FROM system_release_versions") { rs, _ -> rs.getInt("max_version") }
            .first()
}
