package dev.shchuko.marinescreen.domain.usecase

import dev.shchuko.marinescreen.domain.SettingsRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveTermsAcceptedUseCase(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): StateFlow<Boolean> = settingsRepository.termsAcceptedFlow
}

class AcceptTermsUseCase(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke() = settingsRepository.setTermsAccepted()
}

class RejectTermsUseCase(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke() = settingsRepository.setTermsRejected()
}