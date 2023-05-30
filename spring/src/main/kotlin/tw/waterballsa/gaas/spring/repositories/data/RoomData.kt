package tw.waterballsa.gaas.spring.repositories.data

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room

@Document
class RoomData(
    @Id
    var id: String? = null,
    var name: String,
    var status: Room.Status,
    var gameRegistrationId: String,
    @Indexed(unique = true)
    var hostId: String,
    var playerIds: List<String>,
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
                playerIds = players.map { it.id.value },
                maxPlayers = maxPlayers,
                minPlayers = minPlayers,
                name = name,
                status = status,
                isLocked = isLocked,
                password = password
            )
        }
    }

    fun toDomain(gameRegistration: GameRegistration, host: Room.Player, players: MutableList<Room.Player>): Room =
        Room(
            Room.Id(id!!),
            name!!,
            status = status!!,
            gameRegistration,
            host,
            players,
            maxPlayers!!,
            minPlayers!!,
            password = password
        )
}
