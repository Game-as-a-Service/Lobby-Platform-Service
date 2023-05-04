package tw.waterballsa.gaas.domain

class Room(
    var roomId: RoomId? = null,
    val gameId: String,
    val hostId: String,
    val playerIds: List<String>,
    val maxPlayer: Int,
    val minPlayer: Int,
    val name: String,
    val description: String,
) {
    @JvmInline
    value class RoomId(val value: String)
}
