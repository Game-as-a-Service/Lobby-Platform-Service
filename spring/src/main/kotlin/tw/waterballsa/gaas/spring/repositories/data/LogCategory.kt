package tw.waterballsa.gaas.spring.repositories.data

import org.springframework.data.mongodb.core.mapping.Document

@Document
class LogCategory(
    var id: String,
    var type: String
)
