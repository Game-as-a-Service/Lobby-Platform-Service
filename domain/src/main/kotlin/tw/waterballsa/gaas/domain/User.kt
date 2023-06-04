package tw.waterballsa.gaas.domain

class User(
    val id: Id? = null,
    val email: String,
    var nickname: String = "",
) {
    @JvmInline
    value class Id(val value: String)
}
