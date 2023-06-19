package tw.waterballsa.gaas.application.extension

import tw.waterballsa.gaas.domain.Room
import tw.waterballsa.gaas.domain.User
import tw.waterballsa.gaas.domain.Room.Player

internal fun User.toRoomPlayer(): Player =
    Player(Player.Id(id!!.value), nickname)