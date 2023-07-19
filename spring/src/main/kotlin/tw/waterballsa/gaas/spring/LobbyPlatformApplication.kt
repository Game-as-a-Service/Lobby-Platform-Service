package tw.waterballsa.gaas.spring

import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOServer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import tw.waterballsa.gaas.spring.configs.socketio.SocketIOConfig


@SpringBootApplication(scanBasePackages = ["tw.waterballsa.gaas"])
@Import(SocketIOConfig::class)
class LobbyPlatformApplication



fun main(args: Array<String>) {
    runApplication<LobbyPlatformApplication>(*args)
}
