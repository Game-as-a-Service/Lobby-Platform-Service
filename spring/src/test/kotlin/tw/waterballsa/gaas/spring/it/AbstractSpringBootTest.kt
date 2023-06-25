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

@SpringBootTest
@AutoConfigureMockMvc
abstract class AbstractSpringBootTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    final val mockUser: User = User(
        User.Id("1"),
        "user@example.com",
        "user-437b200d-da9c-449e-b147-114b4822b5aa",
        mutableListOf("google-oauth2|102527320242660434908")
    )

    final fun String.toJwt(): Jwt =
        Jwt.withTokenValue("mock-token")
            .header("alg", "none")
            .subject(this)
            .claim("email", mockUser.email)
            .build()

    protected fun <T> ResultActions.getBody(type: Class<T>): T =
        andReturn().response.contentAsString.let { objectMapper.readValue(it, type) }

    protected fun <T> ResultActions.getBody(type: TypeReference<T>): T =
        andReturn().response.contentAsString.let { objectMapper.readValue(it, type) }

    protected fun Any.toJson(): String = objectMapper.writeValueAsString(this)

    protected fun MockHttpServletRequestBuilder.withJson(request: Any): MockHttpServletRequestBuilder =
        contentType(APPLICATION_JSON).content(request.toJson())

    protected fun MockHttpServletRequestBuilder.withJwt(jwt: Jwt): MockHttpServletRequestBuilder =
        with(jwt().jwt(jwt))

}
