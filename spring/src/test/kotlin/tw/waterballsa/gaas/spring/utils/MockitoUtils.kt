package tw.waterballsa.gaas.spring.utils

import org.mockito.Mockito

class MockitoUtils {
    companion object {
        fun <T> anyObject(): T {
            Mockito.any<T>()
            return uninitialized()
        }

        @Suppress("UNCHECKED_CAST")
        private fun <T> uninitialized(): T = null as T
    }

}
