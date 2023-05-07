package tw.waterballsa.gaas.spring.repositories.data

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.Room.*

@Document
class RoomData(
    @Id
    var roomId: String? = null,
    var gameRegistrationId: String?,
    @Indexed(unique = true)
    var hostId: String?,
    var playerIds: List<String>?,
    var maxPlayers: Int?,
    var minPlayers: Int?,
    var name: String?,
    var description: String?,
    var status: Status?,
    var password: String?
) {
    companion object {
        fun Room.toData(): RoomData {
            return RoomData(
                roomId = roomId?.value,
                gameRegistrationId = gameRegistration.id!!.value,
                hostId = host.id.value,
                playerIds = players.map { it.id.value },
                maxPlayers = maxPlayers,
                minPlayers = minPlayers,
                name = name,
                description = description,
                status = status,
                password = password
            )
        }
    }

    fun toDomain(gameRegistration: GameRegistration, host: Player): Room =
        Room(
            Id(roomId!!),
            gameRegistration,
            host,
            listOf(host),
            maxPlayers!!,
            minPlayers!!,
            name!!,
            description!!,
            status = status!!,
            password = password
        )
}
