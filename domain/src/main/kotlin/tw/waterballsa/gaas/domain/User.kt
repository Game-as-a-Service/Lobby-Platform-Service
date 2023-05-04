package tw.waterballsa.gaas.domain

open class User(
    val id: UserId? = null,
    val nickName: String? = null,
    val email: String? = null
) {
    @JvmInline
    value class UserId(val value: String)
}

// Temporary

