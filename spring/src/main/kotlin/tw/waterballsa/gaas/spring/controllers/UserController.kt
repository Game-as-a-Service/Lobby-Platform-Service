package tw.waterballsa.gaas.spring.controllers

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import tw.waterballsa.gaas.application.usecases.CreateUserUseCase
import tw.waterballsa.gaas.application.usecases.GetUserUseCase
import tw.waterballsa.gaas.application.usecases.UpdateUserUseCase
import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.exceptions.enums.PlatformError
import tw.waterballsa.gaas.exceptions.enums.PlatformError.JWT_NOT_FOUND
import tw.waterballsa.gaas.spring.controllers.presenter.GetUserPresenter
import tw.waterballsa.gaas.spring.controllers.presenter.UpdateUserPresenter
import tw.waterballsa.gaas.spring.controllers.viewmodel.GetUserViewModel
import tw.waterballsa.gaas.spring.controllers.viewmodel.PlatformViewModel
import tw.waterballsa.gaas.spring.controllers.viewmodel.UpdateUserViewModel

@RestController
@RequestMapping("/users")
class UserController(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val createUserUseCase: CreateUserUseCase,
) {
    @GetMapping("/me")
    fun getUser(@AuthenticationPrincipal principal: Jwt): GetUserViewModel {
        val request = principal.toRequest()
        val presenter = GetUserPresenter()
        getUserUseCase.execute(request, presenter)
        return presenter.viewModel
    }

    @PutMapping("/me")
    fun updateUser(
        @AuthenticationPrincipal principal: Jwt,
        @RequestBody updateUserRequest: UpdateUserRequest,
    ): UpdateUserViewModel {
        val request = updateUserRequest.toRequest(principal.subject)
        val presenter = UpdateUserPresenter()
        updateUserUseCase.execute(request, presenter)
        return presenter.viewModel
    }

    @PostMapping
    fun createUser(
        @AuthenticationPrincipal principal: Jwt,
        @RequestBody createUserRequest: CreateUserRequest,
    ): PlatformViewModel {
        createUserUseCase.execute(createUserRequest.toRequest(principal.subject))
        return PlatformViewModel.success();
    }
}

private fun Jwt.toRequest(): GetUserUseCase.Request =
    GetUserUseCase.Request(subject)

data class UpdateUserRequest(val nickname: String) {

    fun toRequest(email: String): UpdateUserUseCase.Request =
        UpdateUserUseCase.Request(email, nickname)
}

data class CreateUserRequest(
    val email: String,
) {
    fun toRequest(identityProviderId: String): CreateUserUseCase.Request =
        CreateUserUseCase.Request(email, identityProviderId)
}
