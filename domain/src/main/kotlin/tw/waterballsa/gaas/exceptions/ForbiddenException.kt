package tw.waterballsa.gaas.exceptions

class ForbiddenException private constructor(message: String) : PlatformException(message) {
    companion object {
        fun forbidden(message: Message): ForbiddenExceptionBuilder =
            ForbiddenExceptionBuilder(message)

        class ForbiddenExceptionBuilder(private val message: Message) {
            fun message(): ForbiddenException = ForbiddenException(message.toString())
        }

        enum class Message(val message: String) {
            NOT_IN_ROOM("Not in the room")
        }
    }
}