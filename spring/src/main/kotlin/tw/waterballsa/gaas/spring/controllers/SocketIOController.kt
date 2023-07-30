package tw.waterballsa.gaas.spring.controllers

import com.corundumstudio.socketio.SocketIOServer
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/start")
class SocketIOController(private val socketIOServer: SocketIOServer) {

    @GetMapping("/active")
    fun startSocketIO() {
        socketIOServer.start()
    }

    @GetMapping("/deactive")
    fun stopSocketIO() {
        socketIOServer.stop()
    }


}