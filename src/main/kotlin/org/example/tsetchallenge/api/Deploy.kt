package org.example.tsetchallenge.api

import org.example.tsetchallenge.models.ServiceRelease
import org.example.tsetchallenge.repository.SystemReleaseRepository
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/deploy")
class Deploy(val systemReleaseRepository: SystemReleaseRepository) {

    @RequestMapping(value = [""], method = [RequestMethod.POST], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun createDeployment(@RequestBody serviceRelease: ServiceRelease): Int {
        return systemReleaseRepository.createRelease(serviceRelease)
    }
}