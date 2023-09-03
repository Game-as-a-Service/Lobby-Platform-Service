package tw.waterballsa.gaas.spring.client

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpStatus.OK
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import tw.waterballsa.gaas.application.client.GameService
import tw.waterballsa.gaas.application.client.StartGameRequest
import tw.waterballsa.gaas.application.client.StartGameResponse
import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.exceptions.enums.PlatformError.GAME_START_FAILED

@Component
class RestGameService(
    private val restTemplate: RestTemplate
) : GameService {

    override fun startGame(host: String, jwtToken: String, request: StartGameRequest): StartGameResponse {
        val response = restTemplate.exchange(
            "$host/games",
            POST,
            HttpEntity(request, HttpHeaders().apply { setBearerAuth(jwtToken) }),
            StartGameResponse::class.java
        )
        if (response.statusCode != OK) {
            throw PlatformException(GAME_START_FAILED, "Failed to start game")
        }
        return response.body!!
    }
}
