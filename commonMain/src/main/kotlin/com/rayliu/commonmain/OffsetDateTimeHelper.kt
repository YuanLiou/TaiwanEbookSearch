package com.rayliu.commonmain

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalTime::class)
class OffsetDateTimeHelper {
    fun provideCurrentMoment(): LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    @OptIn(FormatStringsInDatetimeFormats::class)
    fun provideTimeStampString(localDateTime: LocalDateTime): String =
        localDateTime.toInstant(TimeZone.currentSystemDefault()).format(
            DateTimeComponents.Format {
                byUnicodePattern("uuuu-MM-dd'T'HH:mm:ss.SSSxxx")
            }
        )

    fun convertToLocalDateTime(timeStamp: String): LocalDateTime = Instant.parse(timeStamp).toLocalDateTime(TimeZone.currentSystemDefault())
}
