package tw.waterballsa.gaas.spring.repositories

import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import tw.waterballsa.gaas.application.repositories.SocketIORepository
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
class SpringSocketIORepository(
    //private val iSocketIORepository: ISocketIODAO
): SocketIORepository {


    /**
     * Store connected clients
     */
    private val clientMap: MutableMap<String, SocketIOClient> = ConcurrentHashMap()

    /**
     * Custom event `push_data_event` used for server-client communication
     */
    private val PUSH_DATA_EVENT = "push_data_event"

    @Autowired
    private lateinit var socketIOServer: SocketIOServer

    /**
     * Start after Spring IoC container creates and loads the SocketIOServiceImpl bean
     */
    private fun autoStartup() {
        start()
    }

    /**
     * Stop before Spring IoC container destroys the SocketIOServiceImpl bean to avoid port occupation issues on project restart
     */
    private fun autoStop() {
        stop()
    }

    override fun start() {
        // Listen for client connections
        socketIOServer.addConnectListener { client ->
            println("************ Client: ${getIpByClient(client)} has connected ************")
            // Custom event `connected` -> communicate with the client (You can also use built-in events like Socket.EVENT_CONNECT)
            client.sendEvent("connected", "You have successfully connected...")
            val userId = getParamsByClient(client)
            if (userId != null) {
                clientMap[userId] = client
            }
        }

        // Listen for client disconnections
        socketIOServer.addDisconnectListener { client ->
            val clientIp = getIpByClient(client)
            println("$clientIp *********************** Client has disconnected")
            val userId = getParamsByClient(client)
            if (userId != null) {
                clientMap.remove(userId)
                client.disconnect()
            }
        }

        // Custom event `client_info_event` -> Listen for client messages
        socketIOServer.addEventListener(PUSH_DATA_EVENT, String::class.java) { client, data, ackSender ->
            // When the client sends `client_info_event`, onData receives the data here (in this case, it's a string JSON data, but it could also be Byte[], object, or other types)
            val clientIp = getIpByClient(client)
            println("$clientIp ************ Client: $data")
        }

        // Start the server
        socketIOServer.start()

        // broadcast: By default, it broadcasts to all socket connections except the sender. If you want to receive the message yourself, you need to send it separately.
        Thread {
            var i = 0
            while (true) {
                try {
                    // Send a broadcast message every 3 seconds
                    Thread.sleep(3000)
                    socketIOServer.broadcastOperations.sendEvent("myBroadcast", "Broadcast message ")
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    override fun stop() {
        socketIOServer.stop()
    }

    override fun pushMessageToUser(userId: String, msgContent: String) {
        val client = clientMap[userId]
        if (client != null) {
            client.sendEvent(PUSH_DATA_EVENT, msgContent)
        }
    }

    /**
     * Get the `userId` parameter from the client's URL (Modify this based on your needs and client-side implementation)
     *
     * @param client: Client
     * @return: String
     */
    private fun getParamsByClient(client: SocketIOClient): String? {
        // Get client's URL parameters (userId is a unique identifier here)
        val params = client.handshakeData.urlParams
        val userIdList = params["userId"]
        return userIdList?.get(0)
    }

    /**
     * Get the client's connected IP address
     *
     * @param client: Client
     * @return: String
     */
    private fun getIpByClient(client: SocketIOClient): String {
        val sa = client.remoteAddress.toString()
        return sa.substring(1, sa.indexOf(":"))
    }

}