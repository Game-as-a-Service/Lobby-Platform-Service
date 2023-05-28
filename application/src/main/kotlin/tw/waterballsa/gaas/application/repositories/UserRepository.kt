package tw.waterballsa.gaas.application.repositories

import tw.waterballsa.gaas.domain.User

interface UserRepository {
    fun findById(id: User.Id): User?
    fun existsUserByEmail(email: String): Boolean
    fun createUser(user: User): User
    fun deleteAll()
}
