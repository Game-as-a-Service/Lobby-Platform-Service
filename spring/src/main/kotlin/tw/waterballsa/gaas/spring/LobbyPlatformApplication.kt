package tw.waterballsa.gaas.spring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.config.EnableMongoAuditing

@SpringBootApplication(scanBasePackages = ["tw.waterballsa.gaas"])
@EnableMongoAuditing
class LobbyPlatformApplication

fun main(args: Array<String>) {
    runApplication<LobbyPlatformApplication>(*args)
}
