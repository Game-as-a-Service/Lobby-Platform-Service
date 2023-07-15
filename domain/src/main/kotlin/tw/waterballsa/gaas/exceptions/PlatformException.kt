package tw.waterballsa.gaas.exceptions

import tw.waterballsa.gaas.exceptions.enums.PlatformError

open class PlatformException(
    val platformError: PlatformError,
    message: String,
) : RuntimeException(message)
