package tw.waterballsa.gaas.spring.configs.socketio

import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SocketIOEventHandler(
    private val socketIOServer: SocketIOServer) {

//    @Autowired
//    private lateinit var socketIOServer: SocketIOServer

    init {
        configureEventHandlers()
    }

    private fun configureEventHandlers() {
        socketIOServer.addEventListener("chatMessage", String::class.java) { client: SocketIOClient, data: String, _ ->
            // Handle the "chatMessage" event
            println("Received message: $data from client: ${client.sessionId}")
        }

        // Add other event handlers here
    }
}