package tw.waterballsa.gaas.spring.configs.socketio

import com.corundumstudio.socketio.SocketIOServer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableConfigurationProperties(SocketIOProperties::class)
class SocketIOConfig (private val socketIOProperties: SocketIOProperties){

    @Bean
    fun SocketIOConfig(): SocketIOServer {
        val configuration = com.corundumstudio.socketio.Configuration()
        configuration.hostname = socketIOProperties.host
        configuration.port = socketIOProperties.port

        return SocketIOServer(configuration)
    }
}