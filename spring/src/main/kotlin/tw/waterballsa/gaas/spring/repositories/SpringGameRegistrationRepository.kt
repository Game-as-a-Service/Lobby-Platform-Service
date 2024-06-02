package tw.waterballsa.gaas.spring.repositories

import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Order
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

    override fun findGameRegistrations(sortBy: String?): List<GameRegistration> {
        return SortBy.from(sortBy)
            ?.let { Sort.by(it.orders) }
            ?.run { gameRegistrationDAO.findAll(this).map { it.toDomain() } }
            ?: gameRegistrationDAO.findAll().map { it.toDomain() }
    }

    override fun findById(id: Id): GameRegistration? =
        gameRegistrationDAO.findById(id.value).mapOrNull(GameRegistrationData::toDomain)

    override fun updateGame(gameRegistration: GameRegistration): GameRegistration =
        gameRegistrationDAO.save(gameRegistration.toData()).toDomain()

    enum class SortBy(val value: String, val orders: List<Order>) {
        CREATED_ON("createdOn", listOf(Order.desc("createdOn"), Order.desc("_id")));

        companion object {
            private val map = SortBy.values().associateBy { it.value }
            infix fun from(value: String?): SortBy? = value?.let { map[value] }
        }
    }
}
