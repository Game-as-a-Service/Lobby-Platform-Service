package tw.waterballsa.gaas.spring.repositories.data

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import tw.waterballsa.gaas.domain.Room

@Document
class RoomData(
    @Id
    var roomId: String? = null,
    var gameId: String?,
    var hostId: String?,
    var playerIds: List<String>?,
    var maxPlayer: Int?,
    var minPlayer: Int?,
    var name: String?,
    var description: String?,
) {
    companion object {
        fun Room.toData(): RoomData {
            return RoomData(
                roomId = roomId?.value,
                gameId = gameId,
                hostId = hostId,
                playerIds = playerIds,
                maxPlayer = maxPlayer,
                minPlayer = minPlayer,
                name = name,
                description = description
            )
        }
    }

    fun toDomain(): Room =
        Room(
            Room.RoomId(roomId!!),
            gameId!!,
            hostId!!,
            playerIds!!,
            maxPlayer!!,
            minPlayer!!,
            name!!,
            description!!
        )
}