package tw.waterballsa.gaas.spring.repositories

import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.extensions.mapOrNull
import tw.waterballsa.gaas.spring.repositories.dao.UserDAO
import tw.waterballsa.gaas.spring.repositories.data.UserData
import tw.waterballsa.gaas.spring.repositories.data.toData

@Component
class SpringUserRepository(
    private val userDAO: UserDAO
) : UserRepository {
    override fun findById(id: User.Id): User? =
        userDAO.findById(id.value).mapOrNull(UserData::toDomain)

    override fun existsUserByEmail(email: String): Boolean = userDAO.existsByEmail(email)

    override fun createUser(user: User): User = userDAO.save(user.toData()).toDomain()

    override fun deleteAll() {
        userDAO.deleteAll()
    }

    override fun findAllById(ids: Collection<User.Id>): List<User> =
        userDAO.findAllById(ids.map(User.Id::value)).map(UserData::toDomain)

    override fun findByEmail(email: String): User? =
        userDAO.findByEmail(email)?.toDomain()

    override fun update(user: User): User =
        userDAO.save(user.toData()).toDomain()
}
