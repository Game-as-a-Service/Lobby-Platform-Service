package tw.waterballsa.gaas.domain

import tw.waterballsa.gaas.domain.Room.Status.WAITING
import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.exceptions.enums.PlatformError.PLAYER_NOT_FOUND
import tw.waterballsa.gaas.exceptions.enums.PlatformError.PLAYER_NOT_HOST

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
        val player =
            findPlayer(playerId) ?: throw PlatformException(PLAYER_NOT_FOUND, "Player not joined")
        if (readiness) {
            player.ready()
        } else {
            player.cancelReady()
        }
    }

    fun hasPlayer(playerId: Player.Id): Boolean =
        players.any { it.id == playerId }

    fun kickPlayer(hostId: Player.Id, playerId: Player.Id) {
        validateRoomHost(hostId)
        val player =
            findPlayer(playerId) ?: throw PlatformException(PLAYER_NOT_FOUND, "Player not joined")
        players.remove(player)
    }

    fun validateRoomHost(userId: Player.Id) {
        if (host.id != userId) {
            throw PlatformException(PLAYER_NOT_HOST, "Player(${userId.value}) is not the host")
        }
    }

    fun leaveRoom(playerId: Player.Id) {
        players.removeIf { it.id == playerId }
        if (playerId == host.id) {
            changeHost()
        }
    }

    private fun changeHost() {
        players.firstOrNull()
            ?.let {
                host = it
                host.ready()
            }
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
