package tw.waterballsa.gaas.domain
import tw.waterballsa.gaas.domain.Room.Player

class User(
    val id: Id? = null,
    val email: String,
    var nickname: String = "",
) {
    @JvmInline
    value class Id(val value: String)
}
