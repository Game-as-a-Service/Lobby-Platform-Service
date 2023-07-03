package tw.waterballsa.gaas.spring.repositories.data

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import tw.waterballsa.gaas.domain.Room

@Document
class RoomData(
    @Id
    var id: String? = null,
    var name: String,
    var status: Room.Status,
    @DBRef
    var game: GameRegistrationData,
    @Indexed(unique = true)
    var host: PlayerData,
    val players: List<PlayerData>,
    var maxPlayers: Int,
    var minPlayers: Int,
    var password: String?
) {


    fun toDomain(players: MutableList<Room.Player>): Room =
        Room(
            Room.Id(id!!),
            game.toDomain(),
            host.toDomain(),
            players,
            maxPlayers,
            minPlayers,
            name,
            status = status,
            password = password
        )

    class PlayerData(
        val id: String,
        val nickname: String,
        private val readiness: Boolean
    ) {
        fun toDomain(): Room.Player =
            Room.Player(Room.Player.Id(id), nickname, readiness)
    }
}

fun Room.toData(): RoomData =
    RoomData(
        id = roomId?.value,
        name = name,
        status = status,
        game = game.toData(),
        host = host.toData(),
        players = players.map { it.toData() },
        maxPlayers = maxPlayers,
        minPlayers = minPlayers,
        password = password
    )

fun Room.Player.toData(): RoomData.PlayerData = RoomData.PlayerData(id.value, nickname, readiness)
