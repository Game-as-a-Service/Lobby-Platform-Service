package tw.waterballsa.gaas.spring.client

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
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

    override fun startGame(host: String, jwtToken: String, body: StartGameRequest): StartGameResponse {
        val header = HttpHeaders().apply { setBearerAuth(jwtToken) }
        val request = HttpEntity(body, header)
        val response = restTemplate.postForObject("$host/games", request, StartGameResponse::class.java)

        return response ?: throw PlatformException(GAME_START_FAILED, "Failed to start game")
    }
}
