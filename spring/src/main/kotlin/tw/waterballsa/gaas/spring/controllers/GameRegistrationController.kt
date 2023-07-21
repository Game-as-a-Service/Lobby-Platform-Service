package tw.waterballsa.gaas.spring.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tw.waterballsa.gaas.application.usecases.GetGameRegistrationsUsecase
import tw.waterballsa.gaas.application.usecases.Presenter
import tw.waterballsa.gaas.application.usecases.RegisterGameUsecase
import tw.waterballsa.gaas.application.usecases.UpdateGameRegistrationUseCase
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.GameRegistration.*
import tw.waterballsa.gaas.events.DomainEvent
import tw.waterballsa.gaas.events.RegisteredGameEvent
import tw.waterballsa.gaas.spring.controllers.GetGameRegistrationPresenter.*
import tw.waterballsa.gaas.spring.controllers.presenter.UpdateGameRegistrationPresenter
import tw.waterballsa.gaas.spring.controllers.viewmodel.UpdateGameRegistrationViewModel
import tw.waterballsa.gaas.spring.extensions.getEvent

@RestController
@RequestMapping("/games")
class GameRegistrationController(
    private val registerGameUsecase: RegisterGameUsecase,
    private val getGameRegistrationsUsecase: GetGameRegistrationsUsecase,
    private val updateGameRegistrationUseCase: UpdateGameRegistrationUseCase
) {

    @PostMapping
    fun registerGame(@RequestBody request: RegisterGameRequest): ResponseEntity<Any> {
        val presenter = RegisterGamePresenter()
        registerGameUsecase.execute(request.toRequest(), presenter)

        return presenter.viewModel
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.noContent().build()
    }

    @GetMapping
    fun findGameRegistrations(): List<GetGamesViewModel> {
        val presenter = GetGameRegistrationPresenter()
        getGameRegistrationsUsecase.execute(presenter)
        return presenter.viewModel
    }

    @PutMapping("/{gameId}")
    fun updateGameRegistration(
        @PathVariable gameId: String,
        @RequestBody updateGameRegistrationRequest: UpdateGameRegistrationRequest
    ): UpdateGameRegistrationViewModel {
        val request = updateGameRegistrationRequest.toRequest(gameId)
        val presenter = UpdateGameRegistrationPresenter()
        updateGameRegistrationUseCase.execute(request, presenter)
        return presenter.viewModel
    }

    class RegisterGameRequest(
        private val uniqueName: String,
        private val displayName: String,
        private val shortDescription: String,
        private val rule: String,
        private val imageUrl: String,
        private val minPlayers: Int,
        private val maxPlayers: Int,
        private val frontEndUrl: String,
        private val backEndUrl: String
    ) {
        fun toRequest(): RegisterGameUsecase.Request = RegisterGameUsecase.Request(
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
}

class RegisterGamePresenter : Presenter {
    var viewModel: RegisterGameViewModel? = null
        private set

    override fun present(vararg events: DomainEvent) {
        viewModel = events.getEvent(RegisteredGameEvent::class)?.toViewModel()
    }

    private fun RegisteredGameEvent.toViewModel() =
        RegisterGameViewModel(
            id,
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

    data class RegisterGameViewModel(
        val id: Id,
        val uniqueName: String,
        val displayName: String,
        val shortDescription: String,
        val rule: String,
        val imageUrl: String,
        val minPlayers: Int,
        val maxPlayers: Int,
        val frontEndUrl: String,
        val backEndUrl: String
    )
}

class GetGameRegistrationPresenter : GetGameRegistrationsUsecase.Presenter {
    var viewModel = emptyList<GetGamesViewModel>()
        private set

    override fun renderGameRegistrations(gameRegistrations: Collection<GameRegistration>) {
        viewModel = gameRegistrations.map { it.toViewModel() }
    }

    private fun GameRegistration.toViewModel(): GetGamesViewModel =
        GetGamesViewModel(
            id = id!!,
            name = displayName,
            img = imageUrl,
            minPlayers = minPlayers,
            maxPlayers = maxPlayers
        )

    data class GetGamesViewModel(
        val id: Id,
        val name: String,
        val img: String,
        val minPlayers: Int,
        val maxPlayers: Int,
    )
}
