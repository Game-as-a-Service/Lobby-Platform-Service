package tw.waterballsa.gaas.spring.configs.socketio

import com.corundumstudio.socketio.SocketIOServer
import lombok.RequiredArgsConstructor
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
@RequiredArgsConstructor
class ServerCommandLineRunner(private val server: SocketIOServer) : CommandLineRunner {
    //private val log = LoggerFactory.getLogger(ServerCommandLineRunner::class.java)


    @Throws(Exception::class)
    override fun run(vararg args: String?) {
        server!!.start()
    }
}