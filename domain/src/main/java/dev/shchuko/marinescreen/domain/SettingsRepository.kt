package dev.shchuko.marinescreen.domain

import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val termsAcceptedFlow: StateFlow<Boolean>
    suspend fun setTermsAccepted()
    suspend fun setTermsRejected()
}