package tw.waterballsa.gaas.domain

import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.exceptions.enums.PlatformError.USER_INPUT_INVALID

class User(
    val id: Id? = null,
    val email: String = "",
    var nickname: String = "",
    val identities: MutableList<String> = mutableListOf(),
) {
    @JvmInline
    value class Id(val value: String)

    companion object {
        private const val NICKNAME_MINIMUM_BYTE_SIZE = 4
        private const val NICKNAME_MAXIMUM_BYTE_SIZE = 16
    }

    constructor(email: String, nickname: String, identities: MutableList<String>) :
        this(null, email, nickname, identities)

    fun changeNickname(nickname: String) {
        val nicknameByteSize = nickname.toByteArray().size

        if (nicknameByteSize < NICKNAME_MINIMUM_BYTE_SIZE) {
            throw PlatformException(USER_INPUT_INVALID, "invalid nickname: too short")
        }

        if (nicknameByteSize > NICKNAME_MAXIMUM_BYTE_SIZE) {
            throw PlatformException(USER_INPUT_INVALID, "invalid nickname: too long")
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
