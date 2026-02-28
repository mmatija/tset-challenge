package org.example.tsetchallenge

import org.example.tsetchallenge.models.ServiceRelease
import org.example.tsetchallenge.repository.SystemReleaseRepository
import org.example.tsetchallenge.services.ServiceReleaseManager
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

@SpringBootTest
class ServiceReleaseManagerTest(@Autowired private val systemReleaseRepository: SystemReleaseRepository): BaseTest() {

    val serviceReleaseManager = ServiceReleaseManager(systemReleaseRepository)

    @Test
    fun `getServiceReleases returns all service releases for a given system release version`() {
        val serviceRelease1 = ServiceRelease("Service A", 1)
        val serviceRelease2 = ServiceRelease("Service B", 1)
        systemReleaseRepository.createRelease(changeset=serviceRelease1, systemReleaseVersion=1)
        systemReleaseRepository.createRelease(changeset=serviceRelease2, systemReleaseVersion=2)

        val serviceReleases = serviceReleaseManager.getServiceReleases(systemReleaseVersion=2)
        assertEquals(listOf(serviceRelease1, serviceRelease2), serviceReleases.sortedBy { serviceRelease -> serviceRelease.serviceName })
    }
}