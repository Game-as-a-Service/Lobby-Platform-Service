package tw.waterballsa.gaas.spring.aspects

import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import tw.waterballsa.gaas.exceptions.NotFoundException
import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.spring.controllers.viewmodel.PlatformViewModel

@RestControllerAdvice
class PlatformExceptionHandler {

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException::class)
    fun notFound(exception: NotFoundException): PlatformViewModel =
        exception.toPlatformViewModel()

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(PlatformException::class)
    fun badRequest(exception: PlatformException): PlatformViewModel =
        exception.toPlatformViewModel()
}

private fun <T : PlatformException> T.toPlatformViewModel(): PlatformViewModel =
    PlatformViewModel(errorCode = platformError.code, message = message!!)
