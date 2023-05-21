package tw.waterballsa.gaas.application.repositories

import tw.waterballsa.gaas.domain.User

interface UserRepository {
    fun findById(id: User.Id): User?
    fun findByEmail(email: String): User?
    fun existsUserByEmail(email: String): Boolean
    fun existsUserByNickname(nickname: String): Boolean
    fun createUser(user: User): User
    fun updateUser(user: User): User
    fun deleteAll()
}
