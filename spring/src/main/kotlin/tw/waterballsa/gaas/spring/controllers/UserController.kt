package tw.waterballsa.gaas.spring.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import tw.waterballsa.gaas.application.usecases.GetUserUseCase

@RestController
class UserController(
    private val getUserUseCase: GetUserUseCase
) {
    @GetMapping("/users/{id}")
    fun getUser(@PathVariable id: String) = getUserUseCase.execute(id)
}
