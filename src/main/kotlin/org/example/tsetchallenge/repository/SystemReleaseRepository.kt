package org.example.tsetchallenge.repository

import org.example.tsetchallenge.models.ServiceRelease

interface SystemReleaseRepository {
    fun getServiceReleases(systemVersion: Int): List<ServiceRelease>
    fun createRelease(changeset: ServiceRelease, systemReleaseVersion: Int)
}