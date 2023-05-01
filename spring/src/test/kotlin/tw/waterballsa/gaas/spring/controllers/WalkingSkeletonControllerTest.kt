package tw.waterballsa.gaas.spring.controllers

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = ["dev"])
class WalkingSkeletonControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun walkingSkeleton() {
        mockMvc.perform(get("/walking-skeleton"))
            .andExpect(status().isOk)
            .andExpect(content().string("Walk"))
    }
}
