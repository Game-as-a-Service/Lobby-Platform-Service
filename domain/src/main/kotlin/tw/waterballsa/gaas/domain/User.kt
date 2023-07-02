package tw.waterballsa.gaas.domain

class User(
    val id: Id? = null,
    val email: String = "",
    val nickname: String = "",
    val identities: MutableList<String> = mutableListOf(),
) {
    @JvmInline
    value class Id(val value: String)

    fun updateNickname(nickname: String): User {
        return User(id, email, nickname, identities)
    }

    fun hasIdentity(identityProviderId: String): Boolean {
        return identities.contains(identityProviderId)
    }

    fun addIdentity(identityProviderId: String) {
        identities.add(identityProviderId)
    }
}
