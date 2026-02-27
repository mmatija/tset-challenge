package org.example.tsetchallenge

import org.example.tsetchallenge.repository.ReleaseManagerRepository
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertTrue

@SpringBootTest
class SystemReleaseRepositoryTests {

    val releaseManagerRepository = ReleaseManagerRepository()

    @Test
    fun `getServiceReleases returns empty list when there are no service releases for given system version`() {
        val releases = releaseManagerRepository.getServiceReleases(systemVersion=1)
        assertTrue { releases.isEmpty() }
    }

}