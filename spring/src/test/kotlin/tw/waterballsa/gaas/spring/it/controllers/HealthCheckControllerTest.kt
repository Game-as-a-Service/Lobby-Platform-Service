package tw.waterballsa.gaas.spring.it.controllers

import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tw.waterballsa.gaas.spring.it.AbstractSpringBootTest

class HealthCheckControllerTest : AbstractSpringBootTest() {
    @Test
    fun testHealthCheck() {
        mockMvc.perform(get("/health"))
            .andExpect(status().isNoContent)
    }
}