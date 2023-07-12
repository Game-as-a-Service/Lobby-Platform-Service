package tw.waterballsa.gaas.spring.controllers.viewmodel

data class GetRoomViewModel(
    val id: String,
    val name: String,
    val game: Game,
    val host: Player,
    val players: List<Player>,
    val maxPlayers: Int,
    val minPlayers: Int,
    val currentPlayers: Int,
    val isLocked: Boolean,
    val status: String
) {
    data class Game(val id: String, val name: String)

    data class Player(val id: String, val nickname: String, val isReady: Boolean)
}