package tw.waterballsa.gaas.application.extension

import tw.waterballsa.gaas.domain.Room.Player
import tw.waterballsa.gaas.domain.User

internal fun User.toRoomPlayer(): Player =
    Player(Player.Id(id!!.value), nickname)
