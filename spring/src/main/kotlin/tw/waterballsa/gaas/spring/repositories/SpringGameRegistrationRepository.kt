package tw.waterballsa.gaas.spring.repositories

import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.spring.repositories.dao.GameRegistrationDAO
import tw.waterballsa.gaas.spring.repositories.data.GameRegistrationData

@Component
class SpringGameRegistrationRepository(
    private val gameRegistrationDAO: GameRegistrationDAO
) : GameRegistrationRepository {
    override fun registerGame(gameRegistration: GameRegistration): GameRegistration =
        gameRegistrationDAO.save(GameRegistrationData.toData(gameRegistration)).toDomain()

    override fun findGameRegistrationByUniqueName(uniqueName: String): GameRegistration? =
        gameRegistrationDAO.findByUniqueName(uniqueName)?.toDomain()

    override fun deleteAll() {
        gameRegistrationDAO.deleteAll()
    }

    override fun getNumberOfTotalGameRegistrations(): Long = gameRegistrationDAO.count()
}
