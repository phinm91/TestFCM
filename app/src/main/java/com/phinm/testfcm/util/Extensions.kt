package com.phinm.testfcm.util

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.phinm.testfcm.data.EventConfig
import timber.log.Timber
import java.text.SimpleDateFormat
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

fun Long.fromDateToString(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        .format(Date(this))
}

fun LocalTime.toUTCFormat(
    zoneOffset: ZoneOffset = defaultZoneOffset()
): String {
    val offsetTime = this.atOffset(zoneOffset)
    val utcTime = offsetTime.withOffsetSameInstant(ZoneOffset.UTC)
    return utcTime.toString()
}

fun String.fromUTCTimeToLocaleTime(
    zoneOffset: ZoneOffset = defaultZoneOffset()
): String {
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
): String {
    return LocalTime.of(hour, minute).format(
        DateTimeFormatter.ofPattern("HH:mm")
    )
}

fun String.fromLocalTimeToUTCTime(
    zoneId: ZoneId = ZoneId.systemDefault()
): String {
    val localDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse(this))
    val zonedDateTime = localDateTime.atZone(zoneId)
    val utcZonedDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC)

    return utcZonedDateTime.format(DateTimeFormatter.ofPattern("HH:mmX"))
}

fun defaultZoneOffset(): ZoneOffset = ZonedDateTime.now(ZoneId.systemDefault()).offset

fun String.fromLocalTimeStrToLocalTime(pattern: String = "HH:mm"): LocalTime {
    return LocalTime.parse(this, DateTimeFormatter.ofPattern(pattern))
}

fun String.intervalMinutesToLong(): Long {
    if (!this.endsWith(EventConfig.NOTIFICATION_INTERVAL_FORMAT_MINUTES)) return 0
    return this.replace(EventConfig.NOTIFICATION_INTERVAL_FORMAT_MINUTES, "", true).toLong()
}

fun generateTimePoints(
    startTimeStr: String,
    endTimeStr: String,
    intervalMinutesStr: String
): List<String> {
    val startTime = startTimeStr.fromLocalTimeStrToLocalTime()
    val endTime = endTimeStr.fromLocalTimeStrToLocalTime()
    val isOvernight = endTime.isBefore(startTime)
    val intervalMinutes = intervalMinutesStr.intervalMinutesToLong()

    val timePoints = mutableListOf<LocalTime>()
    var current = startTime

    do {
        timePoints.add(current)
        val nextTime = current.plusMinutes(intervalMinutes)

        Timber.v("nextTime check: $nextTime")

        current = nextTime
    } while (
        (!isOvernight && !current.isAfter(endTime))
        || (isOvernight && !(current.isAfter(endTime) && current.isBefore(startTime)))
    )

    Timber.v("timePoints : $timePoints")

    return timePoints.map { it.toUTCFormat() }
}