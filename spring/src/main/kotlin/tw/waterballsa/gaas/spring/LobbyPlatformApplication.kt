package tw.waterballsa.gaas.spring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["tw.waterballsa.gaas"])
class LobbyPlatformApplication

fun main(args: Array<String>) {
    runApplication<LobbyPlatformApplication>(*args)
}
