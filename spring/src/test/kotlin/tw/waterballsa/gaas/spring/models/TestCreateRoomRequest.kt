package tw.waterballsa.gaas.spring.models

class TestCreateRoomRequest(
    val name: String,
    val gameId: String,
    val password: String? = null,
    val maxPlayers: Int,
    val minPlayers: Int,
)
