package tw.waterballsa.gaas.spring.configs.socketio

import com.corundumstudio.socketio.SocketIOServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import javax.annotation.PreDestroy

@Configuration
@Order(1)
@ConditionalOnProperty(prefix = "socketio", name = ["active"], havingValue = "true")
class ServerCommandLineRunner(private val server: SocketIOServer) : CommandLineRunner {

    private val logger: Logger = LoggerFactory.getLogger(ServerCommandLineRunner::class.java)

    override fun run(vararg args: String?) {
        server.start()
    }

    @PreDestroy
    fun stopServer() {
        if (System.getProperty("skipServerCommandLineRunner") == null) {
            println("Stopping the server...")
            logger.info("Stopping the server...")
            server.stop()
            println("Server stopped.")
            server.stop() // Call stop twice to ensure it stops properly.
            logger.info("Server stopped.")
        }
    }
}