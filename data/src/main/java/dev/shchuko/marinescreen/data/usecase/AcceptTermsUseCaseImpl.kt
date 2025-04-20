package dev.shchuko.marinescreen.data.usecase

import dev.shchuko.marinescreen.domain.SettingsRepository
import dev.shchuko.marinescreen.domain.usecase.AcceptTermsUseCase

class AcceptTermsUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : AcceptTermsUseCase {
    override suspend fun invoke() = settingsRepository.setTermsAccepted()
}