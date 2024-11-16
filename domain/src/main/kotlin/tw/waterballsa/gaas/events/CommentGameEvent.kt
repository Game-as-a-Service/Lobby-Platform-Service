package tw.waterballsa.gaas.events

import tw.waterballsa.gaas.domain.GameRegistration
import tw.waterballsa.gaas.domain.User

data class CommentGameEvent(
    val gameId: GameRegistration.Id,
    val userId: User.Id,
    val incrementRating: Long,
    val incrementNumberOfComments: Long,
) : DomainEvent()
