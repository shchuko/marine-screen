package dev.shchuko.marinescreen.domain.usecase

import kotlinx.coroutines.flow.StateFlow

interface ObserveTermsAcceptedUseCase {
    operator fun invoke(): StateFlow<Boolean>
}
