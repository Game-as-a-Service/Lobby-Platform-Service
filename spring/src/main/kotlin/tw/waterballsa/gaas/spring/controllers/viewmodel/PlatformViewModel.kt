package tw.waterballsa.gaas.spring.controllers.viewmodel

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PlatformViewModel(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val errorCode: String? = null,
    val message: String,
) {

    companion object {
        fun success(): PlatformViewModel = PlatformViewModel(message = "success")
    }
}


