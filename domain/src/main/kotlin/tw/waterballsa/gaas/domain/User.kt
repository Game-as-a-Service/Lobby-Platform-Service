package tw.waterballsa.gaas.domain

class User(
    val id: UserId? = null,
    val email: String,
    val nickname: String
) {
    @JvmInline
    value class UserId (val value: String)
}
