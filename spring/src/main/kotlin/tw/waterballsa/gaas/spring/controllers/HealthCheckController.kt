package tw.waterballsa.gaas.spring.controllers

import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController {
    @GetMapping("/health")
    @ResponseStatus(NO_CONTENT)
    fun healthCheck() {
    }
}
