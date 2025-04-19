package dev.shchuko.marinescreen.di

import android.content.Context
import com.example.domain.time.PreciseTimeProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.shchuko.marinescreen.data.NtpClientImpl
import javax.inject.Singleton
import dev.shchuko.marinescreen.data.SettingsRepositoryImpl
import dev.shchuko.marinescreen.data.repository.FakeStationRepository
import dev.shchuko.marinescreen.domain.NtpClient
import dev.shchuko.marinescreen.domain.SettingsRepository
import dev.shchuko.marinescreen.domain.repository.StationRepository
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
    ): PreciseTimeProvider = PreciseTimeProvider(client, scope)

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
}