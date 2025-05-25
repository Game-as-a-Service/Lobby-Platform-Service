package tw.waterballsa.gaas.spring.controllers

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import tw.waterballsa.gaas.application.usecases.CollectGameUseCase
import tw.waterballsa.gaas.application.usecases.GetGameCollectionsUseCase
import tw.waterballsa.gaas.application.usecases.UnCollectGameUseCase
import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.exceptions.enums.PlatformError.JWT_ERROR
import tw.waterballsa.gaas.spring.controllers.presenter.RenderGameRegistrationPresenter
import tw.waterballsa.gaas.spring.controllers.viewmodel.GameRegistrationViewModel
import tw.waterballsa.gaas.spring.controllers.viewmodel.PlatformViewModel

@RequestMapping("/collections")
@RestController
class GameCollectionController(
    private val collectGameUseCase: CollectGameUseCase,
    private val unCollectGameUseCase: UnCollectGameUseCase,
    private val getGameCollectionsUseCase: GetGameCollectionsUseCase,
) {

    @PostMapping("/games/{gameId}")
    fun collectGame(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable gameId: String,
    ): PlatformViewModel {
        collectGameUseCase.execute(CollectGameUseCase.Request(jwt.identityProviderId, gameId))
        return PlatformViewModel.success()
    }

    @DeleteMapping("/games/{gameId}")
    fun unCollectGame(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable gameId: String,
    ): PlatformViewModel {
        unCollectGameUseCase.execute(UnCollectGameUseCase.Request(jwt.identityProviderId, gameId))
        return PlatformViewModel.success()
    }

    @GetMapping
    fun getGameCollections(
        @AuthenticationPrincipal jwt: Jwt,
    ): List<GameRegistrationViewModel> {
        val presenter = RenderGameRegistrationPresenter()
        getGameCollectionsUseCase.execute(
            GetGameCollectionsUseCase.Request(jwt.identityProviderId),
            presenter
        )
        return presenter.viewModel
    }
}

private val Jwt.identityProviderId: String
    get() = subject ?: throw PlatformException(JWT_ERROR, "identityProviderId should exist.")