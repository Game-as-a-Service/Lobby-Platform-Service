package tw.waterballsa.gaas.spring.repositories

import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.repositories.GameRegistrationRepository
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.GameRegistration.Id
import tw.waterballsa.gaas.spring.extensions.mapOrNull
import tw.waterballsa.gaas.spring.repositories.dao.GameRegistrationDAO
import tw.waterballsa.gaas.spring.repositories.data.GameRegistrationData
import tw.waterballsa.gaas.spring.repositories.data.toData

@Component
class SpringGameRegistrationRepository(
    private val gameRegistrationDAO: GameRegistrationDAO
) : GameRegistrationRepository {
    override fun registerGame(gameRegistration: GameRegistration): GameRegistration =
        gameRegistrationDAO.save(gameRegistration.toData()).toDomain()

    override fun findGameRegistrationByUniqueName(uniqueName: String): GameRegistration? =
        gameRegistrationDAO.findByUniqueName(uniqueName)?.toDomain()

    override fun deleteAll() {
        gameRegistrationDAO.deleteAll()
    }

    override fun getNumberOfTotalGameRegistrations(): Long = gameRegistrationDAO.count()

    override fun existsByUniqueName(uniqueName: String): Boolean = gameRegistrationDAO.existsByUniqueName(uniqueName)

    override fun findGameRegistrations(): List<GameRegistration> =
        gameRegistrationDAO.findAll().map(GameRegistrationData::toDomain)

    override fun findById(id: Id): GameRegistration? =
        gameRegistrationDAO.findById(id.value).mapOrNull(GameRegistrationData::toDomain)

    override fun updateGame(gameRegistration: GameRegistration): GameRegistration =
        gameRegistrationDAO.save(gameRegistration.toData()).toDomain()
}
