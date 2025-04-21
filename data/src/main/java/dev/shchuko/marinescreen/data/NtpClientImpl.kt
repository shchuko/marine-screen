package dev.shchuko.marinescreen.data

import dev.shchuko.marinescreen.domain.NtpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import org.apache.commons.net.ntp.NTPUDPClient
import java.io.IOException
import java.net.InetAddress

class NtpClientImpl : NtpClient {
    private val addresses = listOf(
        "time.google.com",
        "time.windows.com",
    )

    override suspend fun getCurrent(): Instant = withContext(Dispatchers.IO) {
        NTPUDPClient().use { client ->
            var lastException: Throwable? = null
            for (address in addresses) {
                try {
                    val timeInfo = client.getTime(InetAddress.getByName(address))
                    return@withContext Instant.fromEpochMilliseconds(timeInfo.message.receiveTimeStamp.time)
                } catch (e: IOException) {
                    lastException = e
                }
            }
            throw checkNotNull(lastException)
        }
    }
}
