package tw.waterballsa.gaas.spring.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tw.waterballsa.gaas.application.usecases.RegisterGameUsecase
import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.events.DomainEvent
import tw.waterballsa.gaas.events.RegisteredGameEvent
import kotlin.reflect.KClass

@RestController
@RequestMapping("/games")
class GameRegistrationController(
    private val registerGameUsecase: RegisterGameUsecase
) {

    @PostMapping
    fun registerGame(@RequestBody request: RegisterGameRequest): ResponseEntity<Any> {
        val presenter = RegisterGamePresenter()
        registerGameUsecase.execute(request.toRequest(), presenter)

        return presenter.getViewModel()
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.noContent().build()
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

class RegisterGamePresenter : RegisterGameUsecase.Presenter {
    private var viewModel: RegisterGameViewModel? = null

    companion object {
        fun <T : DomainEvent> getEvent(events: List<DomainEvent>, type: KClass<T>): T? {
            return events.filter { type.isInstance(it) }
                .map { it as T }
                .firstOrNull()
        }
    }

    override fun present(events: List<DomainEvent>) {
        viewModel = getEvent(events, RegisteredGameEvent::class)
            ?.let {
                RegisterGameViewModel(
                    it.id,
                    it.uniqueName,
                    it.displayName,
                    it.shortDescription,
                    it.rule,
                    it.imageUrl,
                    it.minPlayers,
                    it.maxPlayers,
                    it.frontEndUrl,
                    it.backEndUrl
                )
            }
    }

    fun getViewModel(): RegisterGameViewModel? = viewModel

    data class RegisterGameViewModel(
        val id: GameRegistration.GameRegistrationId,
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
