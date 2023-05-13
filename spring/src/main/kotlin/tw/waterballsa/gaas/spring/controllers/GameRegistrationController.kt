package tw.waterballsa.gaas.spring.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tw.waterballsa.gaas.application.usecases.GetGameRegistrationsUsecase
import tw.waterballsa.gaas.application.usecases.Presenter
import tw.waterballsa.gaas.application.usecases.RegisterGameUsecase
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.events.DomainEvent
import tw.waterballsa.gaas.events.RegisteredGameEvent
import tw.waterballsa.gaas.spring.extensions.getEvent

@RestController
@RequestMapping("/games")
class GameRegistrationController(
    private val registerGameUsecase: RegisterGameUsecase,
    private val getGameRegistrationsUsecase: GetGameRegistrationsUsecase
) {

    @PostMapping
    fun registerGame(@RequestBody request: RegisterGameRequest): ResponseEntity<Any> {
        val presenter = RegisterGamePresenter()
        registerGameUsecase.execute(request.toRequest(), presenter)

        return presenter.getViewModel()
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.noContent().build()
    }

    @GetMapping
    fun findGameRegistrations(): List<GameRegistration> = getGameRegistrationsUsecase.execute()

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
    private var viewModel: RegisterGameViewModel? = null

    override fun present(vararg events: DomainEvent) {
        viewModel = events.getEvent(RegisteredGameEvent::class)?.toViewModel()
    }

    fun getViewModel(): RegisterGameViewModel? = viewModel

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
        val id: GameRegistration.Id,
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
