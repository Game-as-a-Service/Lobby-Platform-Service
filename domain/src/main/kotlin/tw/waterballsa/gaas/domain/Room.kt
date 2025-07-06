package tw.waterballsa.gaas.domain

import tw.waterballsa.gaas.domain.Room.Status.PLAYING
import tw.waterballsa.gaas.domain.Room.Status.WAITING
import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.exceptions.enums.PlatformError.GAME_ALREADY_STARTED
import tw.waterballsa.gaas.exceptions.enums.PlatformError.GAME_NOT_STARTED
import tw.waterballsa.gaas.exceptions.enums.PlatformError.PLAYER_NOT_FOUND
import tw.waterballsa.gaas.exceptions.enums.PlatformError.PLAYER_NOT_HOST
import tw.waterballsa.gaas.exceptions.enums.PlatformError.PLAYER_NOT_IN_ROOM_ERROR

class Room(
    var roomId: Id? = null,
    val game: GameRegistration,
    var host: Player,
    val players: MutableList<Player>,
    val maxPlayers: Int,
    val minPlayers: Int,
    val name: String,
    val password: String? = null,
    var status: Status = WAITING,
) {
    val isLocked: Boolean
        get() = !password.isNullOrEmpty()

    fun addPlayer(player: Player) {
        players.add(player)
    }

    fun isPasswordCorrect(password: String?): Boolean {
        return this.password.equals(password)
    }

    fun isEmpty(): Boolean = players.isEmpty()

    fun isFull(): Boolean = players.size >= maxPlayers

    fun isHost(playerId: Player.Id): Boolean = playerId == host.id

    fun changePlayerReadiness(playerId: Player.Id, readiness: Boolean) {
        val player =
            findPlayer(playerId) ?: throw PlatformException(PLAYER_NOT_FOUND, "Player not joined")
        if (readiness) {
            player.ready()
        } else {
            player.cancelReady()
        }
    }

    fun endGame(player: Player) {
        if (!hasPlayer(player.id)) {
            throw PlatformException(
                PLAYER_NOT_IN_ROOM_ERROR,
                "Player(${player.id.value}) is not in the room(${roomId!!.value}).",
            )
        }
        if (status != PLAYING) {
            throw PlatformException(GAME_NOT_STARTED, "Game has not started yet")
        }
        status = WAITING
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

    fun startGame() {
        if (status != WAITING) {
            throw PlatformException(GAME_ALREADY_STARTED, "Game has already started")
        }
        status = PLAYING
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
