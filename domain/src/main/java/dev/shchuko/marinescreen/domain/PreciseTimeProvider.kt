package dev.shchuko.marinescreen.domain

import dev.shchuko.marinescreen.domain.model.TimeDetails

interface PreciseTimeProvider {
    fun getCurrent(): TimeDetails
}