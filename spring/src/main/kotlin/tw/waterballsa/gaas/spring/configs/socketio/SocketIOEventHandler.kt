package tw.waterballsa.gaas.spring.configs.socketio

import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.DisconnectListener
import com.nimbusds.jose.shaded.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.application.usecases.GetRoomUsecase
import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.events.SocketioEvent
import tw.waterballsa.gaas.spring.controllers.identityProviderId
import tw.waterballsa.gaas.spring.controllers.presenter.GetRoomPresenter


@Component
class SocketIOEventHandler(private val socketIOServer: SocketIOServer,
                           protected val roomRepository: RoomRepository,
                           protected val userRepository: UserRepository,
) {

    private val logger: Logger = LoggerFactory.getLogger(SocketIOEventHandler::class.java)


    init {
        configureEventHandlers()
    }


    fun configureEventHandlers() {

        socketIOServer.addConnectListener { client -> // 判断是否有客户端连接
            if (client != null) {
                logger.info("有新用戶連結  , SessionId: {}", client.getSessionId())
                val board = socketIOServer.broadcastOperations
                logger.info("board clientId {}", board.clients)

            }

        }

        socketIOServer.addEventListener(SocketIOEventName.CHAT_MESSAGE.eventName, SocketioEvent::class.java)
            { client: SocketIOClient, socketioEvent: SocketioEvent, _ ->
            // Handle the "chatMessage" event
            logger.info(" CHAT_MESSAGE Received message: $socketioEvent from client: ${client.sessionId}")

            client.handshakeData.getSingleUrlParam("")
                


            // ECHO
            client.sendEvent(SocketIOEventName.CHAT_MESSAGE.eventName, socketioEvent.data)
        }

        socketIOServer.addEventListener(SocketIOEventName.JOIN_ROOM.eventName, SocketioEvent::class.java) {
                client: SocketIOClient, socketioEvent: SocketioEvent, _ ->

            // ECHO
            logger.info(" JOIN_ROOM Received message: $socketioEvent from client: ${client.sessionId}")
//
            val roomSize = client.getCurrentRoomSize(socketioEvent.data.target)

            // roomSize == 0, create room  else join room
            if(roomSize == 0){
                logger.info("用户：{}", client.sessionId, "you are the host ")
                //client.send()
            } else{
                client.joinRoom(socketioEvent.data.target)
                logger.info("Client joined room: ${socketioEvent.data.target}")
                logger.info("id = " + socketioEvent.data.user.id + " nickname " + socketioEvent.data.user.nickname +  " targetRoom  " + socketioEvent.data.target)
                logger.info(" room size is : ${client.getCurrentRoomSize(socketioEvent.data.target)}")
                socketIOServer.getRoomOperations(socketioEvent.data.target).sendEvent(SocketIOEventName.JOIN_ROOM.eventName, socketioEvent.data.user.id)
            }

        }


        socketIOServer.addEventListener(SocketIOEventName.LEAVE_ROOM.eventName, SocketioEvent::class.java) {
                client: SocketIOClient, socketioEvent: SocketioEvent, _ ->
            // ECHO
            logger.info(" LEAVE_ROOM Received message: ${socketioEvent.data.target} from client: ${client.sessionId}")

            client.leaveRoom(socketioEvent.data.target)

            socketIOServer.removeNamespace(socketioEvent.data.target)
        }


        socketIOServer.addEventListener(SocketIOEventName.DISCONNECT.eventName, SocketioEvent::class.java) {
                client: SocketIOClient, socketioEvent: SocketioEvent, _ ->

            client.disconnect()
            logger.info(" client is leaven room with key disconnect")
        }





        socketIOServer.addDisconnectListener {
                client: SocketIOClient ->
            println("Server disconnected on the server side")
        }





    }
}