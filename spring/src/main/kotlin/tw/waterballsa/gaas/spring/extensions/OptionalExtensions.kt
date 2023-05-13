package tw.waterballsa.gaas.spring.extensions

import java.util.*

fun <T : Any> Optional<T>.orElseNull(): T? = orElse(null)

fun <T : Any, R : Any> Optional<T>.mapOrNull(mapper: (T) -> R): R? = this.map(mapper).orElseNull()
