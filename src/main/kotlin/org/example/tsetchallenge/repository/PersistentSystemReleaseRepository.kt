package org.example.tsetchallenge.repository

import org.example.tsetchallenge.models.ServiceRelease

class PersistentSystemReleaseRepository : SystemReleaseRepository {

    override fun getServiceReleases(systemVersion: Int): List<ServiceRelease> {
        return listOf()
    }

    override fun addNewRelease(changeset: ServiceRelease): Int {
        return 0
    }

}
