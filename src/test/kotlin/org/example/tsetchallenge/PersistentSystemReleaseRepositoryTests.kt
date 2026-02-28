package org.example.tsetchallenge

import org.example.tsetchallenge.models.ServiceRelease
import org.example.tsetchallenge.repository.PersistentSystemReleaseRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
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
        val releases = systemReleaseRepository.getServiceReleases(systemVersion = 1)
        assertTrue { releases.isEmpty() }
    }

    @Nested
    @DisplayName("createRelease")
    inner class CreateRelease {

        @Test
        fun `increments system release version by 1 for given changeset if service release does not exist`() {
            val serviceRelease = ServiceRelease("Service A", 1)
            systemReleaseRepository.createRelease(changeset = serviceRelease)
            val releases = systemReleaseRepository.getServiceReleases(systemVersion = 1)
            assertTrue { releases.size == 1 }
            assertTrue { releases[0] == serviceRelease }
        }

        @Test
        fun `does not increment system release version for given changeset if service release exists`() {
            val serviceRelease1 = ServiceRelease("Service A", 1)
            val serviceRelease2 = ServiceRelease("Service A", 1)
            systemReleaseRepository.createRelease(changeset = serviceRelease1)
            systemReleaseRepository.createRelease(changeset = serviceRelease2)
            val releases = systemReleaseRepository.getServiceReleases(systemVersion = 2)
            assertTrue { releases.isEmpty() }
        }

        @Test
        fun `returns new system release version if service release does not exist`() {
            val serviceRelease = ServiceRelease("Service A", 1)
            assertEquals(1, systemReleaseRepository.createRelease(changeset = serviceRelease))
        }

        @Test
        fun `returns the latest system release version if service release already exists`() {
            val serviceRelease1 = ServiceRelease("Service A", 1)
            val serviceRelease2 = ServiceRelease("Service B", 1)
            val serviceRelease3 = ServiceRelease("Service A", 1)
            systemReleaseRepository.createRelease(changeset = serviceRelease1)
            systemReleaseRepository.createRelease(changeset = serviceRelease2)
            assertEquals(2, systemReleaseRepository.createRelease(changeset = serviceRelease3))
        }

        @Test
        fun `associates all latest service releases with latest system release version`() {
            val serviceRelease1 = ServiceRelease("Service A", 1)
            val serviceRelease2 = ServiceRelease("Service B", 1)
            val serviceRelease3 = ServiceRelease("Service A", 2)
            systemReleaseRepository.createRelease(changeset = serviceRelease1)
            systemReleaseRepository.createRelease(changeset = serviceRelease2)
            systemReleaseRepository.createRelease(changeset = serviceRelease3)
            val releases = systemReleaseRepository.getServiceReleases(systemVersion = 3)
            assertEquals(
                listOf(serviceRelease3, serviceRelease2),
                releases.sortedBy { serviceRelease -> serviceRelease.name })
        }

        @Test
        fun `does not remove association to services released in previous system versions`() {
            val serviceRelease1 = ServiceRelease("Service A", 1)
            val serviceRelease2 = ServiceRelease("Service B", 1)
            val serviceRelease3 = ServiceRelease("Service A", 2)
            systemReleaseRepository.createRelease(changeset = serviceRelease1)
            systemReleaseRepository.createRelease(changeset = serviceRelease2)
            systemReleaseRepository.createRelease(changeset = serviceRelease3)
            val releases = systemReleaseRepository.getServiceReleases(systemVersion = 2)
            assertEquals(
                listOf(serviceRelease1, serviceRelease2),
                releases.sortedBy { serviceRelease -> serviceRelease.name })
        }
    }
}