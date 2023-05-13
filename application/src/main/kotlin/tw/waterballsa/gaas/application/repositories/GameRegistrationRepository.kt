package tw.waterballsa.gaas.application.repositories

import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.GameRegistration.GameRegistrationId

interface GameRegistrationRepository {
    fun registerGame(gameRegistration: GameRegistration): GameRegistration
    fun findGameRegistrationByUniqueName(uniqueName: String): GameRegistration?
    fun deleteAll()
    fun getNumberOfTotalGameRegistrations(): Long
    fun existsByUniqueName(uniqueName: String): Boolean
    fun findGameRegistrations(): List<GameRegistration>
    fun findById(gameRegistrationId: GameRegistrationId): GameRegistration?
}
