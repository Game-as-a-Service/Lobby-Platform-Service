package tw.waterballsa.gaas.spring.aspects

import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import tw.waterballsa.gaas.exceptions.NotFoundException
import tw.waterballsa.gaas.exceptions.PlatformException
import tw.waterballsa.gaas.exceptions.JoinRoomException

@RestControllerAdvice
class PlatformExceptionHandler {
    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException::class)
    fun notFound(exception: NotFoundException): String = exception.message!!

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(PlatformException::class)
    fun badRequest(exception: PlatformException): String = exception.message!!

    @ExceptionHandler(JoinRoomException::class)
    fun validateRoom(exception: JoinRoomException): ResponseEntity<Any> {
        return ResponseEntity(RoomErrorResponse(exception.message ?: ""), BAD_REQUEST)
    }
}

data class RoomErrorResponse(val message: String)
