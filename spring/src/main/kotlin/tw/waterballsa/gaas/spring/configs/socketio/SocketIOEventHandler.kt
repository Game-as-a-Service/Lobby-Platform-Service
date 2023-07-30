package tw.waterballsa.gaas.spring.configs.socketio

import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.nimbusds.jose.shaded.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SocketIOEventHandler(private val socketIOServer: SocketIOServer) {

    private val logger: Logger = LoggerFactory.getLogger(SocketIOEventHandler::class.java)

    init {
        configureEventHandlers()
    }

    private fun configureEventHandlers() {
        socketIOServer.addConnectListener {
            it.sendEvent(SocketIOEventMessage.CONNECT_EVENT.eventName, "HELLO WORLD!")
        }

        socketIOServer.addEventListener(SocketIOEventMessage.CHAT_MESSAGE.eventName, JSONObject::class.java) { client: SocketIOClient, data: JSONObject, _ ->
            // Handle the "chatMessage" event
            logger.info(" Received message: $data from client: ${client.sessionId}")

            // ECHO
            client.sendEvent(SocketIOEventMessage.CHAT_MESSAGE.eventName, data)
        }

        socketIOServer.addEventListener(SocketIOEventMessage.CHATROOM_JOIN.eventName, JSONObject::class.java) {
                client: SocketIOClient, data: JSONObject, _ ->
            // ECHO
            logger.info(" CHATROOM_JOIN message is  " + data)
            client.sendEvent(SocketIOEventMessage.CHATROOM_JOIN.eventName, data)
        }
    }
}