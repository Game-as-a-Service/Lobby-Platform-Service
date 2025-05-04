package tw.waterballsa.gaas.application.repositories

import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.GameRegistration.Id
import tw.waterballsa.gaas.domain.User

interface GameRegistrationRepository {
    fun registerGame(gameRegistration: GameRegistration): GameRegistration
    fun findGameRegistrationByUniqueName(uniqueName: String): GameRegistration?
    fun deleteAll()
    fun getNumberOfTotalGameRegistrations(): Long
    fun existsByUniqueName(uniqueName: String): Boolean
    fun findGameRegistrations(sortBy: String? = null): List<GameRegistration>
    fun findById(id: Id): GameRegistration?
    fun updateGame(gameRegistration: GameRegistration): GameRegistration
    fun findCollectGameRegistrations(userId: User.Id): List<GameRegistration>
}
