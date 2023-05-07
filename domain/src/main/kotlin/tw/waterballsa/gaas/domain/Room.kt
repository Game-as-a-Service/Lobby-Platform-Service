package tw.waterballsa.gaas.domain

import tw.waterballsa.gaas.exceptions.PlayerNumberCompatibilityException

class Room(
    var roomId: Id? = null,
    val gameRegistration: GameRegistration,
    val host: Player,
    val players: List<Player>,
    val maxPlayers: Int,
    val minPlayers: Int,
    val name: String,
    val description: String,
    val status: Status = Status.WAITING,
    val password: String? = null,
) {
    init {
        validatePlayerNumberCompatibility()
    }
   private fun validatePlayerNumberCompatibility() {
       when {
           minPlayers < gameRegistration.minPlayers -> throw PlayerNumberCompatibilityException("The minimum number of players must be equal to or higher than the game's requirement.")
           maxPlayers > gameRegistration.maxPlayers -> throw PlayerNumberCompatibilityException("The maximum number of players must be equal to or less than the game's requirement.")
       }
    }

    @JvmInline
    value class Id(val value: String)

    enum class Status {
        CLOSED, WAITING, PLAYING
    }

    class Player(
        userId: User.UserId,
        val nickname: String,
        var isReady: Boolean = false
    ) {
        val id: Id

        init {
            id = Id(userId.value)
        }

        @JvmInline
        value class Id(val value: String)
    }
}
