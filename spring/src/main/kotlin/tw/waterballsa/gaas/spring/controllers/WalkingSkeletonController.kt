package tw.waterballsa.gaas.spring.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import tw.waterballsa.gaas.application.usecases.WalkingSkeletonUseCase

@RestController
class WalkingSkeletonController(
    private val walkingSkeletonUseCase: WalkingSkeletonUseCase
) {
    @GetMapping("/walking-skeleton")
    fun walk(): String = walkingSkeletonUseCase.execute()
}
