package tw.waterballsa.gaas.spring.repositories

import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.spring.repositories.dao.UserDAO
import tw.waterballsa.gaas.spring.repositories.data.UserData

@Component
class SpringUserRepository(
    private val userDAO: UserDAO
) : UserRepository {
    override fun findUserById(id: String): User? = userDAO.findById(id).orElse(null)?.toDomain()
    override fun createUser(user: User): User = userDAO.save(UserData.toData(user)).toDomain()

    override fun deleteAll() {
        userDAO.deleteAll()
    }
}