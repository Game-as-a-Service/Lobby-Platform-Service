package tw.waterballsa.gaas.spring.it

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions

@SpringBootTest
@AutoConfigureMockMvc
abstract class AbstractSpringBootTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    protected fun <T> ResultActions.getBody(type: Class<T>): T =
        andReturn().response.contentAsString.let { objectMapper.readValue(it, type) }

    protected fun <T> ResultActions.getBody(type: TypeReference<T>): T =
        andReturn().response.contentAsString.let { objectMapper.readValue(it, type) }

    protected fun Any.toJson(): String = objectMapper.writeValueAsString(this)
}
