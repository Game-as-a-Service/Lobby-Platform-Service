package tw.waterballsa.gaas.spring.it

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.utils.Users.Companion.defaultUserBuilder
import java.nio.charset.StandardCharsets.UTF_8
import java.util.UUID.randomUUID

@SpringBootTest
@AutoConfigureMockMvc
abstract class AbstractSpringBootTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    protected final val mockUser: User = defaultUserBuilder("1")
        .nickname("user-${randomUUID()}")
        .identities("google-oauth2|102527320242660434908")
        .build()

    protected final fun String.toJwt(): Jwt = generateJwt(this)

    protected final fun User.toJwt(): Jwt = generateJwt(identities.first())

    private fun generateJwt(id: String): Jwt =
        Jwt.withTokenValue("mock-token")
            .header("alg", "none")
            .subject(id)
            .build()

    protected fun <T> ResultActions.getBody(type: Class<T>): T =
        String(andReturn().response.contentAsByteArray, UTF_8).let { objectMapper.readValue(it, type) }

    protected fun <T> ResultActions.getBody(type: TypeReference<T>): T =
        String(andReturn().response.contentAsByteArray, UTF_8).let { objectMapper.readValue(it, type) }

    protected fun Any.toJson(): String = objectMapper.writeValueAsString(this)

    protected fun MockHttpServletRequestBuilder.withJson(request: Any): MockHttpServletRequestBuilder =
        contentType(APPLICATION_JSON).content(request.toJson())

    protected fun MockHttpServletRequestBuilder.withJwt(jwt: Jwt): MockHttpServletRequestBuilder =
        with(jwt().jwt(jwt))

}
