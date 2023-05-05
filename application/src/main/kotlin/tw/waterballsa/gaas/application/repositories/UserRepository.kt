package tw.waterballsa.gaas.application.repositories

import tw.waterballsa.gaas.domain.User

interface UserRepository {
    fun findUserById(id: String): User?
    fun createUser(user: User) : User
    fun deleteAll()
}
