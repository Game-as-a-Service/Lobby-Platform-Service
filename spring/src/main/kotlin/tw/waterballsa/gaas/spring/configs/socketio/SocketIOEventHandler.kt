package tw.waterballsa.gaas.spring.configs.socketio

import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.nimbusds.jose.shaded.json.JSONObject
import io.swagger.v3.core.util.Json
import org.bson.json.JsonObject
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


        socketIOServer.addEventListener("CHAT_MESSAGE", JSONObject::class.java) { client: SocketIOClient, data: JSONObject, _ ->
            // Handle the "chatMessage" event
            println("Received message: $data from client: ${client.sessionId}")
            println(" CHAT_MESSAGE message is " + data)

            // ECHO
            client.sendEvent("CHAT_MESSAGE", data)
        }

        socketIOServer.addEventListener("CHATROOM_JOIN", JSONObject::class.java) {
                client: SocketIOClient, data: JSONObject, _ ->
            // ECHO
            println(" CHATROOM_JOIN message is  " + data)
            client.sendEvent("CHAT_MESSAGE", data)


        }


        // Add other event handlers here



    }
}