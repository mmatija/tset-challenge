package org.example.tsetchallenge

import org.example.tsetchallenge.models.ServiceRelease
import org.example.tsetchallenge.repository.PersistentSystemReleaseRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
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

    @Test
    fun `createRelease creates new system release for given changeset`() {
        val serviceRelease = ServiceRelease("Service A", 1)
        val systemReleaseVersion = 1
        systemReleaseRepository.addNewRelease(changeset=serviceRelease, systemReleaseVersion)
        val releases = systemReleaseRepository.getServiceReleases(systemVersion=systemReleaseVersion)
        assertTrue { releases.size == 1 }
        assertTrue { releases[0] == serviceRelease }
    }

}