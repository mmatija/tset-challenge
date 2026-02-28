package org.example.tsetchallenge.services

import org.example.tsetchallenge.models.ServiceRelease
import org.example.tsetchallenge.repository.SystemReleaseRepository

class ServiceReleaseManager(val systemReleaseRepository: SystemReleaseRepository) {

    fun getServiceReleases(systemReleaseVersion: Int): List<ServiceRelease> {
        return systemReleaseRepository.getServiceReleases(systemReleaseVersion)
    }
}
