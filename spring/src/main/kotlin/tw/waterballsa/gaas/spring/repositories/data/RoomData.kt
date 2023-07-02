package tw.waterballsa.gaas.spring.repositories.data

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.spring.repositories.data.RoomData.PlayerData.Companion.toData

@Document
class RoomData(
    @Id
    var id: String? = null,
    var name: String,
    var status: Room.Status,
    var gameRegistrationId: String,
    @Indexed(unique = true)
    var hostId: String,
    var players: List<PlayerData>,
    var maxPlayers: Int,
    var minPlayers: Int,
    var isLocked: Boolean,
    var password: String?
) {
    companion object {
        fun Room.toData(): RoomData {
            return RoomData(
                id = roomId?.value,
                gameRegistrationId = game.id!!.value,
                hostId = host.id.value,
                players = players.map { it.toData() },
                maxPlayers = maxPlayers,
                minPlayers = minPlayers,
                name = name,
                status = status,
                isLocked = isLocked,
                password = password
            )
        }
    }

    fun toDomain(game: GameRegistration, host: Room.Player, players: MutableList<Room.Player>): Room =
        Room(
            Room.Id(id!!),
            game,
            host,
            players,
            maxPlayers!!,
            minPlayers!!,
            name!!,
            status = status!!,
            password = password
        )

    class PlayerData(
        val playerId: String,
        val nickname: String,
        val readiness: Boolean
    ) {
        companion object {
            fun Room.Player.toData(): PlayerData = PlayerData(id.value, nickname, readiness)
        }

        fun toDomain(): Room.Player =
            Room.Player(Room.Player.Id(playerId), nickname, readiness)
    }

}
