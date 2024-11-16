package tw.waterballsa.gaas.spring.utils

import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.User

class Users private constructor() {
    companion object {
        fun defaultUserBuilder(id: String): UserBuilder {
            return UserBuilder(User.Id(id))
        }


        fun defaultUser(id: String): User {
            return defaultUserBuilder(id).build()
        }
    }

    class UserBuilder(var id: User.Id) {
        var email: String = "user${id.value}@gmail.com"
        var nickname: String = "user-${id.value}"
        var identities: MutableList<String> = mutableListOf("google-oauth2|${id.value}")
        var lastPlayedGameId: GameRegistration.Id? = null
        var playedGamesIds: Set<GameRegistration.Id>? = null

        fun id(id: String): UserBuilder {
            this.id = User.Id(id)
            return this
        }

        fun email(email: String): UserBuilder {
            this.email = email
            return this
        }

        fun nickname(nickname: String): UserBuilder {
            this.nickname = nickname
            return this
        }

        fun identities(vararg identities: String): UserBuilder {
            this.identities = identities.toMutableList()
            return this
        }

        fun lastPlayedGameId(lastPlayedGameId: GameRegistration.Id?): UserBuilder {
            this.lastPlayedGameId = lastPlayedGameId
            return this
        }

        fun playedGamesIds(playedGamesIds: Set<GameRegistration.Id>?): UserBuilder {
            this.playedGamesIds = playedGamesIds
            return this
        }

        fun build(): User {
            return User(
                id, email, nickname, identities, lastPlayedGameId, playedGamesIds
            )
        }
    }
}