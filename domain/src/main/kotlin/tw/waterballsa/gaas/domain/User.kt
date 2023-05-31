package tw.waterballsa.gaas.domain
import tw.waterballsa.gaas.domain.Room.Player

class User(
    val id: Id? = null,
    val email: String,
    var nickname: String = "",
) {
    fun toRoomPlayer(): Player =
        Player(Player.Id(id!!.value), nickname)

    @JvmInline
    value class Id(val value: String)
}
