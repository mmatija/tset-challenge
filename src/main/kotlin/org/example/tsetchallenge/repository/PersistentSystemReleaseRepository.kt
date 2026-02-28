package org.example.tsetchallenge.repository

import org.example.tsetchallenge.models.ServiceRelease
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository


@Repository
class PersistentSystemReleaseRepository(val jdbcTemplate: JdbcTemplate) : SystemReleaseRepository {

    override fun getServiceReleases(systemVersion: Int): List<ServiceRelease> {
        return jdbcTemplate.query("SELECT * FROM service_releases") { resultSet, _ ->
            ServiceRelease(resultSet.getString("service_name"), resultSet.getInt("service_version"))
        }
    }

    override fun addNewRelease(changeset: ServiceRelease, systemReleaseVersion: Int) {
        try {
            jdbcTemplate.update("INSERT INTO service_releases(service_name, service_version) VALUES (?, ?)", changeset.serviceName, changeset.serviceVersion)
        } catch (e: DuplicateKeyException) {
            throw ServiceReleaseAlreadyExistsException("Release version ${changeset.serviceVersion} for service ${changeset.serviceName} already exists")
        }
    }

}
