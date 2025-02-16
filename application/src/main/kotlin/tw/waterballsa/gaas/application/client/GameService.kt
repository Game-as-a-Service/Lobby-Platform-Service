package tw.waterballsa.gaas.application.client

interface GameService {
    fun startGame(host: String, jwtToken: String, request: StartGameRequest): StartGameResponse
}

data class StartGameRequest(
    val roomId: String,
    val players: List<GamePlayer>,
) {
    data class GamePlayer(
        val id: String,
        // typo for downward compatibility
        val nickName: String,
        val nickname: String,
    )
}

data class StartGameResponse(
    val url: String,
)
