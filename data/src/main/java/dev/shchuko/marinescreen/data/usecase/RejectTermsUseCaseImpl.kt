package dev.shchuko.marinescreen.data.usecase

import dev.shchuko.marinescreen.domain.SettingsRepository
import dev.shchuko.marinescreen.domain.usecase.RejectTermsUseCase

class RejectTermsUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : RejectTermsUseCase {
    override suspend fun invoke() = settingsRepository.setTermsRejected()
}