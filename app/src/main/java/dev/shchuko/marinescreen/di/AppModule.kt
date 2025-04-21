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
import dev.shchuko.marinescreen.data.FakeStationRepository
import dev.shchuko.marinescreen.data.PreciseTimeProviderImpl
import dev.shchuko.marinescreen.domain.usecase.AcceptTermsUseCase
import dev.shchuko.marinescreen.domain.usecase.ObserveStationSettingsUseCase
import dev.shchuko.marinescreen.domain.usecase.ObserveTermsAcceptedUseCase
import dev.shchuko.marinescreen.domain.usecase.ObserveWeatherStationSnapshotUseCase
import dev.shchuko.marinescreen.domain.usecase.RejectTermsUseCase
import dev.shchuko.marinescreen.domain.usecase.UpdateStationSettingsUseCase
import dev.shchuko.marinescreen.domain.NtpClient
import dev.shchuko.marinescreen.domain.PreciseTimeProvider
import dev.shchuko.marinescreen.domain.SettingsRepository
import dev.shchuko.marinescreen.domain.StationRepository
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
        @ApplicationContext context: Context,
    ): StationRepository = FakeStationRepository()

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
    ): ObserveWeatherStationSnapshotUseCase = ObserveWeatherStationSnapshotUseCase(repo)

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
}