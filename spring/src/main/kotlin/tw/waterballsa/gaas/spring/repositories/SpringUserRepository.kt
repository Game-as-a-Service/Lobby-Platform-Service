package tw.waterballsa.gaas.spring.repositories

import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.repositories.dao.UserDAO
import tw.waterballsa.gaas.spring.repositories.data.UserData
import tw.waterballsa.gaas.spring.repositories.data.UserData.Companion.toData

@Component
class SpringUserRepository(
    private val userDAO: UserDAO
) : UserRepository {
    override fun findUserById(id: User.UserId): User? =
        userDAO.findById(id.value).map(UserData::toDomain).orElse(null)

    override fun createUser(user: User): User = userDAO.save(user.toData()).toDomain()

    override fun deleteAll() {
        userDAO.deleteAll()
    }
}