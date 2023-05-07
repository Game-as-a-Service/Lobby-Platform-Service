package tw.waterballsa.gaas.spring.extensions

import java.util.*

fun <T> Optional<T>.orElseNull(): T? = orElse(null)
