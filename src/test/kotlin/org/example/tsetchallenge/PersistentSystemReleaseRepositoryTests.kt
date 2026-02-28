package org.example.tsetchallenge

import org.example.tsetchallenge.models.ServiceRelease
import org.example.tsetchallenge.repository.PersistentSystemReleaseRepository
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertTrue

@SpringBootTest
class PersistentSystemReleaseRepositoryTests {

    val systemReleaseRepository = PersistentSystemReleaseRepository()

    @Test
    fun `getServiceReleases returns empty list when there are no service releases for given system version`() {
        val releases = systemReleaseRepository.getServiceReleases(systemVersion=1)
        assertTrue { releases.isEmpty() }
    }

}