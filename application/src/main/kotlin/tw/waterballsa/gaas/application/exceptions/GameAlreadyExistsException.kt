package tw.waterballsa.gaas.application.exceptions

class GameAlreadyExistsException(name: String) : RuntimeException("$name already exists") {

}
