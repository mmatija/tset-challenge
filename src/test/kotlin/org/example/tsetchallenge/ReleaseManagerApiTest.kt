package org.example.tsetchallenge

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ReleaseManagerApiTest(@Autowired val mockMvc: MockMvc): BaseTest() {

    @Nested
    @DisplayName("GET /services")
    inner class GetReleases {
        @Test
        fun `returns status code 200`() {
            mockMvc.get("/services?systemVersion=1").andExpect { status { isOk() } }
        }

        @Test
        fun `returns status code 400 when system version is not specified`() {
            mockMvc.get("/services").andExpect { status { isBadRequest() } }
        }

        @Test
        fun `returns error message when system version is not specified`() {
            val expectedErrorMessage = """{"message": "systemVersion parameter is missing"}"""
            mockMvc.get("/services").andExpect { content { json(expectedErrorMessage) } }
        }

        @Test
        fun `returns status code 422 when systemVersion is not a positive integer`() {
            mockMvc.get("/services?systemVersion=0").andExpect { status { isUnprocessableContent() } }
        }

        @Test
        fun `returns error message when systemVersion is not positive`() {
            val expectedErrorMessage = """{"message": "systemVersion parameter must be a positive integer"}"""
            mockMvc.get("/services?systemVersion=0").andExpect { content { json(expectedErrorMessage) } }
        }

        @Test
        fun `returns error message when systemVersion is not an integer`() {
            val expectedErrorMessage = """{"message": "systemVersion parameter must be a positive integer"}"""
            mockMvc.get("/services?systemVersion=1.2").andExpect { content { json(expectedErrorMessage) } }
        }
    }

    @Nested
    @DisplayName("POST /deploy")
    inner class CreateRelease {

        @Test
        fun `returns status code 200`() {
            val body = """{"name":"Service A","version":1}"""
            sendRequest(body).andExpect { status { isOk() } }
        }

        @Test
        fun `returns the latest system release version`() {
            val body = """{"name":"Service A","version":1}"""
            sendRequest(body).andExpect { content { string("1") } }
        }

        private fun sendRequest(body: String): ResultActionsDsl = mockMvc.post("/deploy") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }
    }
}