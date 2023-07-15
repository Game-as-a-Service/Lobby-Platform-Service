package tw.waterballsa.gaas.exceptions

import tw.waterballsa.gaas.exceptions.enums.PlatformError
import java.util.*
import kotlin.reflect.KClass

class NotFoundException private constructor(
    platformError: PlatformError,
    message: String,
) : PlatformException(platformError, message) {

    private constructor(platformError: PlatformError, id: Any, identifierName: String, resourceName: String) : this(
        platformError = platformError,
        message = "Resource (${resourceName.capitalize()}) not found ($identifierName = $id).",
    )

    private constructor(platformError: PlatformError, id: Any, resourceName: String) :
        this(platformError, id, "id", resourceName)

    companion object {
        fun <T : Any> notFound(platformError: PlatformError, resourceType: KClass<T>): NotFoundExceptionBuilder =
            notFound(platformError, resourceType.simpleName!!)

        fun notFound(platformError: PlatformError, resourceName: String): NotFoundExceptionBuilder =
            NotFoundExceptionBuilder(platformError, resourceName)

        class NotFoundExceptionBuilder(
            private val platformError: PlatformError,
            private val resourceName: String,
        ) {
            fun identifyBy(identifierName: String, id: Any): NotFoundException =
                NotFoundException(platformError, identifierName, resourceName)

            fun id(id: Any): NotFoundException = NotFoundException(platformError, id, resourceName)

            fun message(): NotFoundException =
                NotFoundException(platformError, "${resourceName.capitalize()} not found")
        }
    }
}

internal fun String.capitalize(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

internal val String.Companion.EMPTY
    get() = ""
