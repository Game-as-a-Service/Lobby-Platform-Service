package tw.waterballsa.gaas.spring.configs.socketio

import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import io.netty.handler.codec.http.HttpHeaderNames
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.eventbus.EventBus
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.events.ChatData
import tw.waterballsa.gaas.events.SocketioEvent
import tw.waterballsa.gaas.events.enums.EventMessageType


@Component
class SocketIOEventHandler(
    private val socketIOServer: SocketIOServer,
    private val eventBus: EventBus,
    protected val roomRepository: RoomRepository,
    protected val userRepository: UserRepository,
) {

    private val logger: Logger = LoggerFactory.getLogger(SocketIOEventHandler::class.java)


    init {
        configureEventHandlers()
    }


    private final fun configureEventHandlers() {

        socketIOServer.addConnectListener { client ->
            val token =  client.handshakeData.httpHeaders.get(HttpHeaderNames.COOKIE)
            val customHeader = client.handshakeData.getSingleUrlParam("Authorization")

            if (client != null ) {
                logger.info("有新用戶連結  , SessionId: {}", client.sessionId)
                val board = socketIOServer.broadcastOperations
                logger.info("board clientId {}", board.clients)
            }
        }

        socketIOServer.addEventListener(EventMessageType.GAME_STARTED.eventName, SocketioEvent::class.java)
        {  client: SocketIOClient, socketioEvent: SocketioEvent, _ ->

            logger.info(" ... " )
            client.sendEvent(EventMessageType.GAME_STARTED.eventName, socketioEvent.data)

        }





        socketIOServer.addEventListener(SocketIOEventName.CHAT_MESSAGE.eventName, SocketioEvent::class.java)
            { client: SocketIOClient, socketioEvent: SocketioEvent, _ ->
            // Handle the "chatMessage" event
            logger.info(" CHAT_MESSAGE Received message: $socketioEvent from client: ${client.sessionId}")
            client.handshakeData.getSingleUrlParam("")
            // ECHO
            client.sendEvent(SocketIOEventName.CHAT_MESSAGE.eventName, socketioEvent.data)

            socketIOServer.broadcastOperations.sendEvent("test", socketioEvent.data)

        }

        socketIOServer.addEventListener(SocketIOEventName.JOIN_ROOM.eventName, ChatData::class.java) {
                client: SocketIOClient, socketioEvent: ChatData, _ ->

            client.joinRoom(socketioEvent.target)
            logger.info("Client joined room: ${socketioEvent.target}")
            logger.info("id = " + socketioEvent.user.id + " nickname " + socketioEvent.user.nickname +  " targetRoom  " + socketioEvent.target)
            logger.info(" room size is : ${client.getCurrentRoomSize(socketioEvent.target)}")
        }


        socketIOServer.addEventListener(SocketIOEventName.LEAVE_ROOM.eventName, SocketioEvent::class.java) {
                client: SocketIOClient, socketioEvent: SocketioEvent, _ ->
            // ECHO
            logger.info(" LEAVE_ROOM Received message: ${socketioEvent.data.target} from client: ${client.sessionId}")

            client.leaveRoom(socketioEvent.data.target)
            socketIOServer.removeNamespace(socketioEvent.data.target)
        }


        socketIOServer.addEventListener(SocketIOEventName.DISCONNECT.eventName, SocketioEvent::class.java) {
                client: SocketIOClient, _: SocketioEvent, _ ->

            client.disconnect()
            logger.info(" client is leaven room with key disconnect")
        }

        socketIOServer.addDisconnectListener {

        logger.info("Server disconnected on the server side")
        }
    }
}
