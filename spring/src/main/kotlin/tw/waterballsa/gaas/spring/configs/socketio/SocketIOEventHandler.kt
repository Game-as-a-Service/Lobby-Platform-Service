package tw.waterballsa.gaas.spring.configs.socketio

import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

class UserMessage(user: UserVM)
class UserVM(id: String, nickname: String)

@Component
class SocketIOEventHandler(
    private val socketIOServer: SocketIOServer) {

//    @Autowired
//    private lateinit var socketIOServer: SocketIOServer

    init {
        configureEventHandlers()
    }

    private fun configureEventHandlers() {
        socketIOServer.addConnectListener {
            it.sendEvent("CHAT_MESSAGE", "HELLO WORLD!")
        }


        socketIOServer.addEventListener("CHAT_MESSAGE", String::class.java) { client: SocketIOClient, data: String, _ ->
            // Handle the "chatMessage" event
            println("Received message: $data from client: ${client.sessionId}")

            // ECHO
            client.sendEvent("CHAT_MESSAGE", data)
        }

        socketIOServer.addEventListener("CHATROOM_JOIN", UserMessage::class.java) {
            client: SocketIOClient, data: UserMessage, _ ->
            // ECHO
            client.sendEvent("CHAT_MESSAGE", data)
        }


        // Add other event handlers here
    }
}