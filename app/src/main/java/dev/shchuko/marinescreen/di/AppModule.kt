package dev.shchuko.marinescreen.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.shchuko.marinescreen.data.NtpClientImpl
import javax.inject.Singleton
import dev.shchuko.marinescreen.data.SettingsRepositoryImpl
import dev.shchuko.marinescreen.data.PreciseTimeProviderImpl
import dev.shchuko.marinescreen.data.station.windguru.WindGuruStationRepository
import dev.shchuko.marinescreen.domain.usecase.AcceptTermsUseCase
import dev.shchuko.marinescreen.domain.usecase.ObserveStationSettingsUseCase
import dev.shchuko.marinescreen.domain.usecase.ObserveTermsAcceptedUseCase
import dev.shchuko.marinescreen.domain.usecase.ObserveStationMeasurementsUseCase
import dev.shchuko.marinescreen.domain.usecase.RejectTermsUseCase
import dev.shchuko.marinescreen.domain.usecase.UpdateStationSettingsUseCase
import dev.shchuko.marinescreen.domain.NtpClient
import dev.shchuko.marinescreen.domain.PreciseTimeProvider
import dev.shchuko.marinescreen.domain.SettingsRepository
import dev.shchuko.marinescreen.domain.StationRepository
import dev.shchuko.marinescreen.domain.usecase.ObserveScreenScaleSettingUseCase
import dev.shchuko.marinescreen.domain.usecase.UpdateScreenScaleSettingUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppScope(): CoroutineScope = CoroutineScope(SupervisorJob())

    @Provides
    @Singleton
    fun provideNtpClient(): NtpClient = NtpClientImpl()

    @Provides
    @Singleton
    fun providePreciseTimeProvider(
        client: NtpClient,
        scope: CoroutineScope
    ): PreciseTimeProvider = PreciseTimeProviderImpl(client, scope)

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository = SettingsRepositoryImpl(context)

    @Provides
    @Singleton
    fun provideStationRepository(
        coroutineScope: CoroutineScope,
        settingsRepository: SettingsRepository,
        preciseTimeProvider: PreciseTimeProvider,
    ): StationRepository = WindGuruStationRepository(
        coroutineScope = coroutineScope,
        settingsRepository = settingsRepository,
        preciseTimeProvider = preciseTimeProvider,
    )

    @Provides
    @Singleton
    fun provideAcceptTermsUseCase(
        repo: SettingsRepository,
    ): AcceptTermsUseCase = AcceptTermsUseCase(repo)

    @Provides
    @Singleton
    fun provideRejectTermsUseCase(
        repo: SettingsRepository,
    ): RejectTermsUseCase = RejectTermsUseCase(repo)

    @Provides
    @Singleton
    fun provideObserveTermsAcceptedUseCase(
        repo: SettingsRepository,
    ): ObserveTermsAcceptedUseCase = ObserveTermsAcceptedUseCase(repo)

    @Provides
    @Singleton
    fun provideObserveStationSnapshotUseCase(
        repo: StationRepository
    ): ObserveStationMeasurementsUseCase = ObserveStationMeasurementsUseCase(repo)

    @Provides
    @Singleton
    fun provideObserveStationSettingsUseCase(
        repo: SettingsRepository
    ): ObserveStationSettingsUseCase = ObserveStationSettingsUseCase(repo)

    @Provides
    @Singleton
    fun provideUpdateStationSettingsUseCase(
        repo: SettingsRepository
    ): UpdateStationSettingsUseCase = UpdateStationSettingsUseCase(repo)


    @Provides
    @Singleton
    fun provideObserveScreenScaleSettingUseCase(
        repo: SettingsRepository
    ): ObserveScreenScaleSettingUseCase = ObserveScreenScaleSettingUseCase(repo)


    @Provides
    @Singleton
    fun provideUpdateScreenScaleSettingUseCase(
        repo: SettingsRepository
    ): UpdateScreenScaleSettingUseCase = UpdateScreenScaleSettingUseCase(repo)

}