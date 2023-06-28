package tw.waterballsa.gaas.exceptions

import java.util.*
import kotlin.reflect.KClass

class NotFoundException private constructor(message: String) : PlatformException(message) {
    private constructor(id: Any, identifierName: String, resourceName: String) : this(
        "Resource (${resourceName.capitalize()}) not found ($identifierName = $id).",
    )

    private constructor(id: Any, resourceName: String) : this(id, "id", resourceName)

    private constructor(message: String, resourceName: String): this(
        "Resource (${resourceName.capitalize()}) not found (${resourceName.capitalize()} = $message)."
    )

    companion object {
        fun <T : Any> notFound(resourceType: KClass<T>): NotFoundExceptionBuilder = notFound(resourceType.simpleName!!)

        fun notFound(resourceName: String): NotFoundExceptionBuilder = NotFoundExceptionBuilder(resourceName)

        class NotFoundExceptionBuilder(private val resourceName: String) {
            fun identifyBy(identifierName: String, id: Any): NotFoundException =
                NotFoundException(identifierName, resourceName)

            fun id(id: Any): NotFoundException = NotFoundException(id, resourceName)

            fun message(messageObj: Any): NotFoundException = message(messageObj.toString())

            fun message(message: String): NotFoundException = NotFoundException(message, resourceName)

            fun shortMessage(): NotFoundException = NotFoundException("${resourceName.capitalize()} not found")
        }
    }
}

internal fun String.capitalize(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

internal val String.Companion.EMPTY
    get() = ""
