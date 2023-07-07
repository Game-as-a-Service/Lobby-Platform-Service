package tw.waterballsa.gaas.domain

import tw.waterballsa.gaas.exceptions.PlatformException

class User(
    val id: Id? = null,
    val email: String = "",
    var nickname: String = "",
    val identities: MutableList<String> = mutableListOf(),
) {
    @JvmInline
    value class Id(val value: String)

    private val nicknameByteSizeMinimum: Int = 4
    private val nicknameByteSizeMaximum: Int = 16

    fun updateNickname(nickname: String) {
        if (nickname.toByteArray().size < nicknameByteSizeMinimum) {
            throw PlatformException("invalid nickname: too short")
        }

        if (nickname.toByteArray().size > nicknameByteSizeMaximum) {
            throw PlatformException("invalid nickname: too long")
        }

        this.nickname = nickname
    }

    fun hasIdentity(identityProviderId: String): Boolean {
        return identities.contains(identityProviderId)
    }

    fun addIdentity(identityProviderId: String) {
        identities.add(identityProviderId)
    }
}
