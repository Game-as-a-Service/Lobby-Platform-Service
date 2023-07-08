package tw.waterballsa.gaas.domain

import tw.waterballsa.gaas.domain.Room.Status.WAITING
import tw.waterballsa.gaas.exceptions.PlatformException

class Room(
    var roomId: Id? = null,
    val game: GameRegistration,
    var host: Player,
    val players: MutableList<Player>,
    val maxPlayers: Int,
    val minPlayers: Int,
    val name: String,
    val password: String? = null,
    val status: Status = WAITING,
) {
    val isLocked: Boolean
        get() = !password.isNullOrEmpty()

    fun addPlayer(player: Player) {
        players.add(player)
    }

    fun isPasswordCorrect(password: String?): Boolean {
        return this.password.equals(password)
    }

    fun isFull(): Boolean = players.size >= maxPlayers

    fun changePlayerReadiness(playerId: Player.Id, readiness: Boolean) {
        val player = findPlayer(playerId) ?: throw PlatformException("Player not joined")
        if (readiness) {
            player.ready()
        } else {
            player.cancelReady()
        }
    }

    fun kickPlayer(hostId: Player.Id, playerId: Player.Id) {
        if (hostId != host.id) {
            throw throw throw PlatformException("This Player is not host")
        }
        val player = findPlayer(playerId) ?: throw PlatformException("Player not joined")
        players.remove(player)
    }

    fun leaveRoom(playerId: Player.Id) {
        players.removeIf { it.id == playerId }
        if (playerId == host.id) {
            changeHost()
        }
    }

    private fun changeHost() {
        players.firstOrNull()
            ?.let { host = it }
    }

    private fun findPlayer(playerId: Player.Id): Player? = players.find { it.id == playerId }

    @JvmInline
    value class Id(val value: String)

    enum class Status {
        WAITING, PLAYING
    }

    class Player(
        val id: Id,
        val nickname: String,
        readiness: Boolean = false,
    ) {
        var readiness: Boolean = readiness
            private set

        fun ready() {
            readiness = true
        }

        fun cancelReady() {
            readiness = false
        }

        @JvmInline
        value class Id(val value: String)
    }
}
