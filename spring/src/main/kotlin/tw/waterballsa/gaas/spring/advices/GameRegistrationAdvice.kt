package tw.waterballsa.gaas.spring.advices

import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GameRegistrationAdvice {
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(RuntimeException::class)
    fun badRequest(exception: RuntimeException): String? = exception.message
}
