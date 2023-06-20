package tw.waterballsa.gaas.spring.controllers.viewmodel

data class GetRoomsViewModel(
    val rooms: List<RoomViewModel>,
    val meta: Meta
) {
    data class RoomViewModel(
        val id: String,
        val name: String,
        val game: Game,
        val host: Player,
        val maxPlayers: Int,
        val minPlayers: Int,
        val currentPlayers: Int,
        val isLocked: Boolean,
    ) {
        data class Game(val id: String, val name: String)
        data class Player(val id: String, val nickname: String)
    }

    data class Meta(
        val total: Int,
        val page: Int,
        val offset: Int
    )
}
