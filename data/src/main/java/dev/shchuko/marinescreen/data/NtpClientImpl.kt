package dev.shchuko.marinescreen.data

import dev.shchuko.marinescreen.domain.NtpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import org.apache.commons.net.ntp.NTPUDPClient
import java.io.IOException
import java.net.InetAddress
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class NtpClientImpl : NtpClient {
    private val addresses = listOf(
        "time.google.com",
        "time.windows.com",
    )

    override suspend fun getCurrent(): Instant = withContext(Dispatchers.IO) {
        NTPUDPClient().use { client ->
            client.setDefaultTimeout(5.seconds.toJavaDuration())
            var lastException: Throwable? = null
            for (address in addresses) {
                try {
                    val timeInfo = client.getTime(InetAddress.getByName(address))
                    return@withContext Instant.fromEpochMilliseconds(timeInfo.returnTime + timeInfo.offset)
                } catch (e: IOException) {
                    lastException = e
                }
            }
            throw checkNotNull(lastException)
        }
    }
}
