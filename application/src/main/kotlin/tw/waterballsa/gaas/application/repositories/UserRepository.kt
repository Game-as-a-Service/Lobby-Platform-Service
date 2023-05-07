package tw.waterballsa.gaas.application.repositories

import tw.waterballsa.gaas.domain.User

interface UserRepository {
    fun findUserById(id: User.UserId): User?
    fun createUser(user: User) : User
    fun deleteAll()
}
