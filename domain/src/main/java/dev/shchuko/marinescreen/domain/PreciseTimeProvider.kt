package dev.shchuko.marinescreen.domain

import dev.shchuko.marinescreen.domain.model.PreciseTime

interface PreciseTimeProvider {
    fun getCurrent(): PreciseTime
}