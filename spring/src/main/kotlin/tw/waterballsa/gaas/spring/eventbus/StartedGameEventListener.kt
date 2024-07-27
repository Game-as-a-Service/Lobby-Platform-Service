package tw.waterballsa.gaas.spring.eventbus

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import tw.waterballsa.gaas.events.StartedGameEvent
import tw.waterballsa.gaas.spring.repositories.dao.GameRegistrationDAO
import kotlin.reflect.KClass

@Component
class StartedGameEventListener(
    override val eventType: KClass<StartedGameEvent>,
    private val gameRegistrationDAO: GameRegistrationDAO,
) : EventListener<StartedGameEvent> {

    @Autowired
    constructor(gameRegistrationDAO: GameRegistrationDAO): this(StartedGameEvent::class, gameRegistrationDAO)

    override fun onEvents(events: List<StartedGameEvent>) {
        events.forEach {
            gameRegistrationDAO.incrementTimesPlayedById(it.data.gameId.value)
        }
    }
}