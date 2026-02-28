package org.example.tsetchallenge.models

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class ServiceRelease(
    @field:NotBlank
    val name: String,

    @field:Min(1)
    val version: Int
) {

}