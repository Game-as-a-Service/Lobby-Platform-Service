package tw.waterballsa.gaas.application.repositories

import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.domain.User.Id

interface UserRepository {
    fun findById(id: Id): User?
    fun existsUserByEmail(email: String): Boolean
    fun createUser(user: User): User
    fun deleteAll()
    fun findAllById(ids: Collection<Id>): List<User>
    fun findByEmail(email: String): User?
    fun update(user: User): User
}
