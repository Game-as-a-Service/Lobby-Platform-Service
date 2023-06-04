package tw.waterballsa.gaas.spring.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import tw.waterballsa.gaas.application.usecases.GetUserUseCase
import tw.waterballsa.gaas.domain.User

@RestController
class UserController(
    private val getUserUseCase: GetUserUseCase
) {
    @GetMapping("/users/{id}")
    fun getUser(@PathVariable id: String): User = getUserUseCase.execute(User.Id(id))

    @GetMapping("/users/{id}/nickname")
    fun checkNicknameExist(@PathVariable id: String): User? =  getUserUseCase.checkNickNameExist(User.Id(id))
}
