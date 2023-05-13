package tw.waterballsa.gaas.spring.it.controllers

import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tw.waterballsa.gaas.spring.it.AbstractSpringBootTest

class WalkingSkeletonControllerTest : AbstractSpringBootTest() {

    @Test
    fun walkingSkeleton() {
        mockMvc.perform(get("/walking-skeleton"))
            .andExpect(status().isOk)
            .andExpect(content().string("Walk"))
    }
}
