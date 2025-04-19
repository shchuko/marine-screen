package dev.shchuko.marinescreen.domain

import kotlinx.datetime.Instant

interface NtpClient {
    suspend fun getCurrent(): Instant
}