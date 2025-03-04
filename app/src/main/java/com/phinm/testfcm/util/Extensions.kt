package com.phinm.testfcm.util

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@SuppressLint("HardwareIds")
fun Context.deviceUUID(): String =
    Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

fun Long.toUTCISOFormat(): String {
    return Instant.ofEpochMilli(this)  // Chuyển từ milliseconds thành Instant
        .atOffset(ZoneOffset.UTC)      // Đưa về múi giờ UTC
        .format(DateTimeFormatter.ISO_INSTANT) // Định dạng theo ISO 8601
}

fun Long.fromDateToString(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        .format(Date(this))
}

fun LocalTime.toUTCFormat(
    zoneOffset: ZoneOffset = defaultZoneOffset()
) : String {
    val offsetTime = this.atOffset(zoneOffset)
    val utcTime = offsetTime.withOffsetSameInstant(ZoneOffset.UTC)
    return utcTime.toString()
}

fun String.fromUTCTimeToLocaleTime(
    zoneOffset: ZoneOffset = defaultZoneOffset()
) : String {
    // Bước 1: Phân tích chuỗi "09:20Z" thành OffsetTime - Thời gian UTC
    val utcTime = OffsetTime.parse(this, DateTimeFormatter.ofPattern("HH:mmX"))

    // Bước 3: Chuyển đổi thời gian sang múi giờ mới
    val convertedTime = utcTime.withOffsetSameInstant(zoneOffset)
    return convertedTime.toLocalTime().format(
        DateTimeFormatter.ofPattern("HH:mm")
    )
}

fun fromLocalTimeToString(
    hour: Int, minute: Int
) : String {
    return LocalTime.of(hour, minute).format(
        DateTimeFormatter.ofPattern("HH:mm")
    )
}

fun String.fromLocalTimeToUTCTime(
    zoneId: ZoneId = ZoneId.systemDefault()
) : String {
    val localDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse(this))
    val zonedDateTime = localDateTime.atZone(zoneId)
    val utcZonedDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC)

    return utcZonedDateTime.format(DateTimeFormatter.ofPattern("HH:mmX"))
}

fun String.convertUTCISOtoDeviceTimeZone(
    pattern: String = "yyyy-MM-dd HH:mm",
    zoneId: ZoneId = ZoneId.systemDefault()
): String {
    return try {
        val instant = Instant.parse(this)
        val formatter =
            DateTimeFormatter.ofPattern(pattern)
        val localDateTime = instant.atZone(zoneId).toLocalDateTime()
        localDateTime.format(formatter)
    } catch (e: Exception) {
        e.printStackTrace()
        "Invalid Time Format"
    }
}

fun defaultZoneOffset() : ZoneOffset = ZonedDateTime.now(ZoneId.systemDefault()).offset