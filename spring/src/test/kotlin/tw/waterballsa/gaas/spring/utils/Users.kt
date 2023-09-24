package tw.waterballsa.gaas.spring.utils

import tw.waterballsa.gaas.domain.User

class Users private constructor() {
    companion object {
        fun defaultUserBuilder(id: String): UserBuilder {
            return UserBuilder(User.Id(id))
        }


        fun defaultUser(id: String): User{
            return defaultUserBuilder(id).build()
        }
    }

    class UserBuilder(var id: User.Id) {
        var email: String = "user${id.value}@gmail.com"
        var nickname: String = "user-${id.value}"
        var identities: MutableList<String> = mutableListOf("google-oauth2|${id.value}")

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

        fun build(): User{
            return User(
                id, email, nickname, identities
            )
        }
    }
}