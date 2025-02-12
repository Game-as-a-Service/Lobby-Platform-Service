package tw.waterballsa.gaas.spring.configs.socketio

import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(SocketIOProperties::class)
class SocketIOConfig{
    private val logger: Logger = LoggerFactory.getLogger(SocketIOConfig::class.java)

    @Bean
    fun socketIOServer(
        socketIOProperties: SocketIOProperties,
    ): SocketIOServer {
        val configuration = com.corundumstudio.socketio.Configuration().apply {
            hostname = socketIOProperties.host
            port = socketIOProperties.port
            allowHeaders = "Authorization, CustomHeaderName"
        }
        logger.info("SocketIO server host: ${configuration.hostname}")
        return SocketIOServer(configuration)
    }

    @Bean
    fun springAnnotationScanner(socketIOServer: SocketIOServer): SpringAnnotationScanner {
        return SpringAnnotationScanner(socketIOServer)
    }
}