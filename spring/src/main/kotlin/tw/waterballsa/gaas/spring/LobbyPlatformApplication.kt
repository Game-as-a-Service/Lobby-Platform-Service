package tw.waterballsa.gaas.spring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import tw.waterballsa.gaas.spring.configs.socketio.SocketIOConfig


@SpringBootApplication(scanBasePackages = ["tw.waterballsa.gaas"])
class LobbyPlatformApplication

fun main(args: Array<String>) {
    runApplication<LobbyPlatformApplication>(*args)
}
