package dev.shchuko.marinescreen.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import dev.shchuko.marinescreen.data.SettingsRepositoryImpl
import dev.shchuko.marinescreen.data.repository.FakeStationRepository
import dev.shchuko.marinescreen.domain.SettingsRepository
import dev.shchuko.marinescreen.domain.repository.StationRepository

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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