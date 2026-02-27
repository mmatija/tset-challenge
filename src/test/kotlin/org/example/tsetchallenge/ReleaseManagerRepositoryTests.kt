package org.example.tsetchallenge

import org.example.tsetchallenge.repository.ReleaseManagerRepository
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertTrue

@SpringBootTest
class ReleaseManagerRepositoryTests {

    val releaseManagerRepository = ReleaseManagerRepository()

    @Test
    fun `getReleases returns empty list when there are no releases for given system version`() {
        val releases = releaseManagerRepository.getReleases(systemVersion=1)
        assertTrue { releases.isEmpty() }
    }

}