package org.example.tsetchallenge.repository

import org.example.tsetchallenge.models.ServiceRelease

interface SystemReleaseRepository {
    fun getServiceReleases(systemVersion: Int) : List<ServiceRelease>
    fun addNewRelease(changeset: ServiceRelease, systemReleaseVersion: Int)
}