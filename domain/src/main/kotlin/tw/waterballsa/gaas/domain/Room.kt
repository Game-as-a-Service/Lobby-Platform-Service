package tw.waterballsa.gaas.domain

import tw.waterballsa.gaas.exceptions.PlayerNumberCompatibilityException

class Room (
    val id: Id? = null,
    val name: String,
    val status: Status = Status.WAITING,
    val gameRegistration: GameRegistration,
    val host: Player,
    val players: MutableList<Player>,
    val maxPlayers: Int,
    val minPlayers: Int,
    val password: String? = null,
){
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
        WAITING, PLAYING
    }

    class Player(
        userId: User.Id,
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