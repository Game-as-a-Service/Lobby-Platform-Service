package tw.waterballsa.gaas.application.client

interface GameService {
    fun startGame(host: String, jwtToken: String, request: StartGameRequest): StartGameResponse
}

data class StartGameRequest(
    val players: List<GamePlayer>,
) {
    data class GamePlayer(
        val id: String,
        val nickName: String,
    )
}

data class StartGameResponse(
    val url: String,
)
