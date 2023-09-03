package tw.waterballsa.gaas.application.client

interface GameService {
    fun startGame(host: String, jwtToken: String, request: StartGameRequest): StartGameResponse
}

data class StartGameRequest(
    val players: List<StartGamePlayer>
)

data class StartGamePlayer(
    val id: String,
    val nickName: String
)

class StartGameResponse(
    val url: String
)
