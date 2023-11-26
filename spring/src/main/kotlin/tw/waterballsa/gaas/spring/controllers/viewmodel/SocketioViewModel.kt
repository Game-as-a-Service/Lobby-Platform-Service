package tw.waterballsa.gaas.spring.controllers.viewmodel

import tw.waterballsa.gaas.events.ChatData
import tw.waterballsa.gaas.events.SocketioEvent

data class SocketioViewModel(

    val datas : List<SocketioEvent>
){
    data class SocketioEvent(val type: String, val data: ChatData)

}