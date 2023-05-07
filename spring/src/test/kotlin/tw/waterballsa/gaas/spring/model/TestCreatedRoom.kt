package tw.waterballsa.gaas.spring.model

class TestCreateRoomRequest(
    val gameId: String,
    val hostId: String,
    val maxPlayers: Int,
    val minPlayers: Int,
    val name: String,
    val description: String,
    val password: String? = null
) {
    fun toJson(): String {
        val password = this.password ?: ""
        return """
            {
                "gameRegistrationId": "$gameId",
                "hostId": "$hostId",
                "maxPlayers": $maxPlayers,
                "minPlayers": $minPlayers,
                "name": "$name",
                "description": "$description",
                "password": "$password"
            }
        """.trimIndent()
    }
}

