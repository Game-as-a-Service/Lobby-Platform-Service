package tw.waterballsa.gaas.spring.eventbus

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import tw.waterballsa.gaas.events.StartedGameEvent
import tw.waterballsa.gaas.spring.repositories.dao.GameRegistrationDAO
import tw.waterballsa.gaas.spring.repositories.dao.UserDAO
import kotlin.reflect.KClass

@Component
class StartedGameEventListener(
    override val eventType: KClass<StartedGameEvent>,
    private val gameRegistrationDAO: GameRegistrationDAO,
    private val userDAO: UserDAO,
) : EventListener<StartedGameEvent> {

    @Autowired
    constructor(gameRegistrationDAO: GameRegistrationDAO, userDAO: UserDAO): this(StartedGameEvent::class, gameRegistrationDAO, userDAO)

    override fun onEvents(events: List<StartedGameEvent>) {
        events.forEach {
            gameRegistrationDAO.incrementTimesPlayedById(it.data.gameId.value)
            it.data.players.forEach { player ->
                userDAO.setLastPlayedGameIdAndAddToSetPlayedGamesIdsById(player.id, it.data.gameId.value)
            }
        }
    }
}