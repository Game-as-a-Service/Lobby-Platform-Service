package tw.waterballsa.gaas.application.repositories

import tw.waterballsa.gaas.domain.GameRegistration

interface GameRegistrationRepository {
    fun registerGame(gameRegistration: GameRegistration): GameRegistration
    fun findGameRegistrationByUniqueName(uniqueName: String): GameRegistration?
    fun deleteAll()
    fun getNumberOfTotalGameRegistrations(): Long
    fun existsByUniqueName(uniqueName: String): Boolean
}
