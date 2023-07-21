package tw.waterballsa.gaas.spring.controllers

import tw.waterballsa.gaas.application.usecases.UpdateGameRegistrationUseCase
import tw.waterballsa.gaas.domain.GameRegistration

data class UpdateGameRegistrationRequest(
    private val uniqueName: String,
    private val displayName: String,
    private val shortDescription: String,
    private val rule: String,
    private val imageUrl: String,
    private val minPlayers: Int,
    private val maxPlayers: Int,
    private val frontEndUrl: String,
    private val backEndUrl: String,
) {
    fun toRequest(gameId: String): UpdateGameRegistrationUseCase.Request = UpdateGameRegistrationUseCase.Request(
        GameRegistration.Id(gameId),
        uniqueName,
        displayName,
        shortDescription,
        rule,
        imageUrl,
        minPlayers,
        maxPlayers,
        frontEndUrl,
        backEndUrl
    )
}