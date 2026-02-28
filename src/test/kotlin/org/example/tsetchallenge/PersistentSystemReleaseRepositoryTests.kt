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
        fun `associates all latest service releases with given system release version`() {
            val serviceRelease1 = ServiceRelease("Service A", 1)
            val serviceRelease2 = ServiceRelease("Service B", 1)
            val serviceRelease3 = ServiceRelease("Service A", 2)
            systemReleaseRepository.createRelease(changeset=serviceRelease1, systemReleaseVersion=1)
            systemReleaseRepository.createRelease(changeset=serviceRelease2, systemReleaseVersion=2)
            systemReleaseRepository.createRelease(changeset=serviceRelease3, systemReleaseVersion=3)
            val releases = systemReleaseRepository.getServiceReleases(systemVersion=3)
            assertEquals(listOf(serviceRelease3, serviceRelease2),releases.sortedBy { serviceRelease -> serviceRelease.serviceName } )
        }

        @Test
        fun `does not remove association to services released in previous system versions`() {
            val serviceRelease1 = ServiceRelease("Service A", 1)
            val serviceRelease2 = ServiceRelease("Service B", 1)
            val serviceRelease3 = ServiceRelease("Service A", 2)
            systemReleaseRepository.createRelease(changeset=serviceRelease1, systemReleaseVersion=1)
            systemReleaseRepository.createRelease(changeset=serviceRelease2, systemReleaseVersion=2)
            systemReleaseRepository.createRelease(changeset=serviceRelease3, systemReleaseVersion=3)
            val releases = systemReleaseRepository.getServiceReleases(systemVersion=2)
            assertEquals(listOf(serviceRelease1, serviceRelease2),releases.sortedBy { serviceRelease -> serviceRelease.serviceName } )
        }

        @Test
        fun `throws an exception if service release already exists`() {
            val serviceRelease = ServiceRelease("Service A", 1)
            systemReleaseRepository.createRelease(changeset=serviceRelease, systemReleaseVersion=1)
            val exception = assertThrows<ServiceReleaseAlreadyExistsException> { systemReleaseRepository.createRelease(changeset=serviceRelease, systemReleaseVersion=2) }
            assertEquals("Release version ${serviceRelease.serviceVersion} for service ${serviceRelease.serviceName} already exists", exception.message)
        }

        @Test
        fun `throws an exception if system release version already exists`() {
            val serviceRelease1 = ServiceRelease("Service A", 1)
            val systemReleaseVersion = 1
            systemReleaseRepository.createRelease(changeset=serviceRelease1, systemReleaseVersion)
            val serviceRelease2 = ServiceRelease("Service A", 2)
            val exception = assertThrows<SystemReleaseVersionAlreadyExistsException> { systemReleaseRepository.createRelease(changeset=serviceRelease2, systemReleaseVersion) }
            assertEquals("System release version $systemReleaseVersion already exists",exception.message)
        }
    }
}