package tw.waterballsa.gaas.spring.controllers.viewmodel

data class PlatformViewModel(
    val message: String
) {
    companion object {
        fun success(): PlatformViewModel = PlatformViewModel("success")
    }
}