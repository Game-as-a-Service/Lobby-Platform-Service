package tw.waterballsa.gaas.domain

class User(
    val id: Id? = null,
    val email: String = "",
    val nickname: String = "",
    val identities: List<String> = emptyList(),
) {
    @JvmInline
    value class Id(val value: String)

    fun hasIdentity(identityProviderId: String): Boolean {
        return identities.contains(identityProviderId)
    }

    fun addIdentity(identityProviderId: String): User =
        User(id, email, nickname, identities + identityProviderId)
}
