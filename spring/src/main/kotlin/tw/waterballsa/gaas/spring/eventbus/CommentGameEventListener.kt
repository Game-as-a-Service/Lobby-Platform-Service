package tw.waterballsa.gaas.spring.eventbus

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import tw.waterballsa.gaas.events.CommentGameEvent
import tw.waterballsa.gaas.spring.repositories.dao.GameRegistrationDAO
import kotlin.reflect.KClass

@Component
class CommentGameEventListener(
    override val eventType: KClass<CommentGameEvent>,
    private val gameRegistrationDAO: GameRegistrationDAO,
) : EventListener<CommentGameEvent> {

    @Autowired
    constructor(gameRegistrationDAO: GameRegistrationDAO): this(CommentGameEvent::class, gameRegistrationDAO)

    override fun onEvents(events: List<CommentGameEvent>) {
        events.forEach {
            gameRegistrationDAO.incrementTotalRatingAndNumberOfCommentsById(
                it.gameId.value, it.incrementRating, it.incrementNumberOfComments
            )
        }
    }
}