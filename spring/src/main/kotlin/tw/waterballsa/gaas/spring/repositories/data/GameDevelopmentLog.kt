package tw.waterballsa.gaas.spring.repositories.data

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "gameDevelopmentLog")
class GameDevelopmentLog(
    @Id
    var id: String? = null,
    var title: String,
    @CreatedDate
    val createdDate: LocalDate,
    val author: String,
    val tags: MutableList<String>,
    var contents: String,
    @DBRef
    var category: LogCategory
)
