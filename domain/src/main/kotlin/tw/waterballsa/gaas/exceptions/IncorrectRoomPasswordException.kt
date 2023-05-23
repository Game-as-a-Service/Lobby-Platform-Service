package tw.waterballsa.gaas.exceptions

class IncorrectRoomPasswordException(password: String) : PlatformException("$password already exists")