package com.bold.trading.core.common.formatters

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Standardizes time to Indian Standard Time (IST, UTC+05:30) as required by PRD Section 9.
 */
object DateTimeFormatterUtils {

    val IST_ZONE: ZoneId = ZoneId.of("Asia/Kolkata")

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss 'IST'", Locale.ENGLISH)
        .withZone(IST_ZONE)

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm IST", Locale.ENGLISH)
        .withZone(IST_ZONE)

    fun formatCurrentIst(): String {
        return timeFormatter.format(Instant.now())
    }

    fun formatIst(instant: Instant): String {
        return dateTimeFormatter.format(instant)
    }

    fun formatIstTime(epochMillis: Long): String {
        return timeFormatter.format(Instant.ofEpochMilli(epochMillis))
    }
}
