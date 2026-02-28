package org.example.tsetchallenge.api

import org.example.tsetchallenge.models.ServiceRelease
import org.example.tsetchallenge.repository.SystemReleaseRepository
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid

@RestController
@RequestMapping("/deploy")
class Deploy(val systemReleaseRepository: SystemReleaseRepository) {

    @RequestMapping(value = [""], method = [RequestMethod.POST], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createDeployment(@Valid @RequestBody serviceRelease: ServiceRelease): Int {
        return systemReleaseRepository.createRelease(serviceRelease)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String>> {
        val message = ex.bindingResult.fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        val response = mapOf("message" to message)
        return ResponseEntity(response, HttpStatus.UNPROCESSABLE_ENTITY)
    }
}