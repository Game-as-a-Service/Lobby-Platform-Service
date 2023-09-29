package tw.waterballsa.gaas.spring.configs.socketio

import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.DisconnectListener
import com.nimbusds.jose.shaded.json.JSONObject
import io.netty.handler.codec.http.HttpHeaderNames
import org.apache.catalina.manager.util.SessionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.repositories.RoomRepository
import tw.waterballsa.gaas.application.repositories.UserRepository
import tw.waterballsa.gaas.application.usecases.GetRoomUsecase
import tw.waterballsa.gaas.domain.Room
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
                logger.info("有新用户连接, SessionId: {}", client.getSessionId())
                val board = socketIOServer.broadcastOperations
                logger.info("board clientId {}", board.clients)

            } else {
                logger.error("并没有人连接上。。。")
            }

        }

        socketIOServer.addEventListener(SocketIOEventName.CHAT_MESSAGE.eventName, JSONObject::class.java)
            { client: SocketIOClient, chatMessage: JSONObject, _ ->
            // Handle the "chatMessage" event
            logger.info(" CHAT_MESSAGE Received message: $chatMessage from client: ${client.sessionId}")

            client.handshakeData.getSingleUrlParam("")


            val jsonObject = JSONObject(chatMessage)

            val map: Map<String, Any> = jsonObject.toMap()
            val type = map["type"]
            val data = map["data"] as? Map<String, Any>

            val target = data?.get("target")


            // ECHO
            client.sendEvent(SocketIOEventName.CHAT_MESSAGE.eventName, data)
        }

        socketIOServer.addEventListener(SocketIOEventName.JOIN_ROOM.eventName, JSONObject::class.java) {
                client: SocketIOClient, getJoinRoom: JSONObject, _ ->

            // ECHO
            logger.info(" JOIN_ROOM Received message: $getJoinRoom from client: ${client.sessionId}")

            val jsonObject = JSONObject(getJoinRoom)

            val map: Map<String, Any> = jsonObject.toMap()
            val user = map["user"]
            val targetRoom = map["target"]

            val userInfo = JSONObject(user as MutableMap<String, *>?)
            val userInfoMap: Map<String, Any> = userInfo.toMap()

            val id = userInfoMap["id"]
            val nickname = userInfoMap["nickname"]

            val target = map?.get("target")

            // need mongo
//            val roomId = Room.Id(target.toString())
//            val room = roomRepository.findById(roomId)
//
            val roomSize = client.getCurrentRoomSize(target.toString())

            // roomSize == 0, create room  else join room
            if(roomSize == 0){
                logger.info("用户：{}", client.sessionId, "you are the host ")
                //client.send()
            } else{
                client.joinRoom(target.toString())
                logger.info("Client joined room: ${target.toString()}")
                logger.info("id = " + id + " nickname " + nickname +  " targetRoom  " + targetRoom)
                logger.info(" room size is : ${client.getCurrentRoomSize(target.toString())}")
                socketIOServer.getRoomOperations(target.toString()).sendEvent(SocketIOEventName.JOIN_ROOM.eventName, id)
            }

        }


        socketIOServer.addEventListener(SocketIOEventName.LEAVE_ROOM.eventName, JSONObject::class.java) {
                client: SocketIOClient, leaveRoom: JSONObject, _ ->
            // ECHO
            logger.info(" LEAVE_ROOM Received message: $leaveRoom from client: ${client.sessionId}")

            val jsonObject = JSONObject(leaveRoom)

            val map: Map<String, Any> = jsonObject.toMap()
            val type = map["type"]
            val data = map["data"] as? Map<String, Any>

            val target = data?.get("target")

            client.leaveRoom(target.toString())

            socketIOServer.removeNamespace(target.toString())
        }


        socketIOServer.addEventListener("DISCONNECT", JSONObject::class.java) {
                client: SocketIOClient, leaveRoom: JSONObject, _ ->

            client.disconnect()
            println(" client is leaven room with key disconnect")
        }





        socketIOServer.addDisconnectListener {
                client: SocketIOClient ->
            println("Server disconnected on the server side")
        }





    }
}