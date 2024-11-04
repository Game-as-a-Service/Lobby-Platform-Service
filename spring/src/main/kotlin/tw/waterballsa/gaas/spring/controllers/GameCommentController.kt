package tw.waterballsa.gaas.spring.controllers

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import tw.waterballsa.gaas.application.usecases.CommentGameUseCase
import tw.waterballsa.gaas.application.usecases.UpdateGameCommentUseCase
import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.exceptions.enums.PlatformError.JWT_ERROR
import tw.waterballsa.gaas.spring.controllers.viewmodel.PlatformViewModel

@RestController
@RequestMapping("/comments")
class GameCommentController(
    private val commentGameUserCase: CommentGameUseCase,
    private val updateGameCommentUseCase: UpdateGameCommentUseCase,
) {

    @PostMapping
    fun commentGame(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: CommentGameRequest
    ): PlatformViewModel {
        commentGameUserCase.execute(
            CommentGameUseCase.Request(
                jwt.identityProviderId,
                request.gameId,
                request.rating,
                request.comment
            )
        )
        return PlatformViewModel.success()
    }


    @PostMapping("/games/{gameId}")
    fun updateGameComment(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable gameId: String,
        @RequestBody request: UpdateGameCommentRequest
    ): PlatformViewModel {
        updateGameCommentUseCase.execute(
            UpdateGameCommentUseCase.Request(
                jwt.identityProviderId,
                gameId,
                request.rating,
                request.comment,
            )
        )
        return PlatformViewModel.success()
    }


    data class CommentGameRequest(
        val gameId: String,
        val rating: Int,
        val comment: String,
    )

    data class UpdateGameCommentRequest(
        val rating: Int,
        val comment: String,
    )
}

private val Jwt.identityProviderId: String
    get() = subject ?: throw PlatformException(JWT_ERROR, "identityProviderId should exist.")