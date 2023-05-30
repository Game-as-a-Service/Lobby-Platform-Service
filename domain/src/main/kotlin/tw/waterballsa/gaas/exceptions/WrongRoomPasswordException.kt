package tw.waterballsa.gaas.exceptions

class WrongRoomPasswordException(password: String) : PlatformException("wrong password")