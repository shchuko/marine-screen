package dev.shchuko.marinescreen.data.usecase

import dev.shchuko.marinescreen.domain.SettingsRepository
import dev.shchuko.marinescreen.domain.usecase.ObserveTermsAcceptedUseCase
import kotlinx.coroutines.flow.StateFlow

class ObserveTermsAcceptedUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : ObserveTermsAcceptedUseCase {
    override fun invoke(): StateFlow<Boolean> = settingsRepository.termsAcceptedFlow
}
