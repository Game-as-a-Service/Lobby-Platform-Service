package tw.waterballsa.gaas.domain

import tw.waterballsa.gaas.domain.Room.Status.WAITING

class Room(
    var roomId: Id? = null,
    val game: GameRegistration,
    val host: Player,
    val players: MutableList<Player>,
    val maxPlayers: Int,
    val minPlayers: Int,
    val name: String,
    val password: String? = null,
    val status: Status = WAITING,
) {
    val isLocked: Boolean
        get() = !password.isNullOrEmpty()

    fun addPlayer(player: Player){
        players.add(player)
    }

    fun isPasswordCorrect(password: String?): Boolean{
        return this.password.equals(password)
    }

    fun isFull(): Boolean = players.size >= maxPlayers

    @JvmInline
    value class Id(val value: String)

    enum class Status {
        WAITING, PLAYING
    }

    class Player(
        val id: Id,
        val nickname: String,
    ) {
        @JvmInline
        value class Id(val value: String)
    }
}
