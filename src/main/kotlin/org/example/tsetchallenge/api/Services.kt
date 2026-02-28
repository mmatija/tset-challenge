package org.example.tsetchallenge.api

import jakarta.validation.constraints.Positive
import org.example.tsetchallenge.models.ServiceRelease
import org.example.tsetchallenge.repository.SystemReleaseRepository
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestController
@RequestMapping("/services")
class Services(val systemReleaseRepository: SystemReleaseRepository) {

    @RequestMapping(value = [""], method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getServices(@RequestParam @Positive systemVersion: Int): List<ServiceRelease> {
        return systemReleaseRepository.getServiceReleases(systemVersion)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingRequestParameterException(): ResponseEntity<Map<String, String>> {
        val response = buildErrorResponse("systemVersion parameter is missing")
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HandlerMethodValidationException::class)
    fun handleNonPositiveParameterException(): ResponseEntity<Map<String, String>> {
        val response = buildTypeMismatchError()
        return ResponseEntity(response, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleNonIntegerParameterException(): ResponseEntity<Map<String, String>> {
        val response = buildTypeMismatchError()
        return ResponseEntity(response, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    private fun buildTypeMismatchError(): HashMap<String, String> =
        buildErrorResponse("systemVersion parameter must be a positive integer")

    private fun buildErrorResponse(errorMessage: String): HashMap<String, String> {
        val response = HashMap<String, String>()
        response["message"] = errorMessage
        return response
    }
}