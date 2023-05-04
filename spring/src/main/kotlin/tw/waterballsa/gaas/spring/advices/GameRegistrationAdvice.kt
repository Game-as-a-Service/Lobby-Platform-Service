package tw.waterballsa.gaas.spring.advices

import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import tw.waterballsa.gaas.application.exceptions.GameAlreadyExistsException

@RestControllerAdvice
class GameRegistrationAdvice {
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException::class, IllegalStateException::class, GameAlreadyExistsException::class)
    fun badRequest(exception: RuntimeException): String? = exception.message
}
