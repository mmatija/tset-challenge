package org.example.tsetchallenge

import org.example.tsetchallenge.models.ServiceRelease
import org.example.tsetchallenge.repository.PersistentSystemReleaseRepository
import org.example.tsetchallenge.repository.ServiceReleaseAlreadyExistsException
import org.example.tsetchallenge.repository.SystemReleaseVersionAlreadyExistsException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
class PersistentSystemReleaseRepositoryTests : BaseTest() {

    @Autowired
lateinit var systemReleaseRepository: PersistentSystemReleaseRepository

    @Test
    fun `getServiceReleases returns empty list when there are no service releases for given system version`() {
        val releases = systemReleaseRepository.getServiceReleases(systemVersion=1)
        assertTrue { releases.isEmpty() }
    }

    @Nested
    @DisplayName("createRelease")
    inner class CreateRelease {

        @Test
        fun `creates new system release for given changeset`() {
            val serviceRelease = ServiceRelease("Service A", 1)
            val systemReleaseVersion = 1
            systemReleaseRepository.createRelease(changeset=serviceRelease, systemReleaseVersion)
            val releases = systemReleaseRepository.getServiceReleases(systemVersion=systemReleaseVersion)
            assertTrue { releases.size == 1 }
            assertTrue { releases[0] == serviceRelease }
        }

        @Test
        fun `throws an exception if service release already exists`() {
            val serviceRelease = ServiceRelease("Service A", 1)
            systemReleaseRepository.createRelease(changeset=serviceRelease, systemReleaseVersion=1)
            val exception = assertThrows<ServiceReleaseAlreadyExistsException> { systemReleaseRepository.createRelease(changeset=serviceRelease, systemReleaseVersion=2) }
            assertEquals(exception.message, "Release version ${serviceRelease.serviceVersion} for service ${serviceRelease.serviceName} already exists")
        }

        @Test
        fun `throws an exception if system release version already exists`() {
            val serviceRelease1 = ServiceRelease("Service A", 1)
            val systemReleaseVersion = 1
            systemReleaseRepository.createRelease(changeset=serviceRelease1, systemReleaseVersion)
            val serviceRelease2 = ServiceRelease("Service A", 2)
            val exception = assertThrows<SystemReleaseVersionAlreadyExistsException> { systemReleaseRepository.createRelease(changeset=serviceRelease2, systemReleaseVersion) }
            assertEquals(exception.message, "System release version $systemReleaseVersion already exists")
        }
    }


}