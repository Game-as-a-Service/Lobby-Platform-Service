package tw.waterballsa.gaas.application.exceptions

import tw.waterballsa.gaas.exceptions.PlatformException

class GameAlreadyExistsException(name: String) : PlatformException("$name already exists")

