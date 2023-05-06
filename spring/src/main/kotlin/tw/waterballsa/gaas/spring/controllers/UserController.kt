package tw.waterballsa.gaas.spring.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import tw.waterballsa.gaas.application.usecases.GetUserUseCase
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.exceptions.NotFoundException

@RestController
class UserController(
    private val getUserUseCase: GetUserUseCase
) {
    @GetMapping("/users/{id}")
    fun getUser(@PathVariable id: String): User = getUserUseCase.execute(id)

    @ExceptionHandler
    fun handleUserNotFoundException(exception: NotFoundException): ResponseEntity<String> =
        ResponseEntity(exception.message, HttpStatus.NOT_FOUND)
}
