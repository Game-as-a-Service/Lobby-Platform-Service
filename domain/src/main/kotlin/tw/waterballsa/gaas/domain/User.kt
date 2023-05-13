package tw.waterballsa.gaas.domain

class User(
    val id: Id? = null,
    val email: String,
    val nickname: String,
) {
    @JvmInline
    value class Id(val value: String)
}
