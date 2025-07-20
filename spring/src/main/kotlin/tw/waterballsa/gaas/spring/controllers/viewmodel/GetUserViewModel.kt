package tw.waterballsa.gaas.spring.controllers.viewmodel

data class GetUserViewModel(
    val id: String,
    val email: String,
    val nickname: String,
    val lastPlayedGameId: String?,
    val playedGamesIds: Set<String>?,
    val currentGameRoomId: String?,
    val currentGameUrl: String?,
)
