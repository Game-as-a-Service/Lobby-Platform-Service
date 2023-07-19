package tw.waterballsa.gaas.spring.controllers

import com.corundumstudio.socketio.SocketIOServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/socket-io")
class SocketIOController (

){

    @Autowired
    private lateinit var socketIOServer: SocketIOServer


    @PostMapping("/sendMessage")
    fun sendMessage(@RequestParam userId: String?, @RequestParam msgContent: String?) {
        socketIOServer.broadcastOperations.sendEvent("chatMessage", "Hello, Socket.IO!")

    }

}