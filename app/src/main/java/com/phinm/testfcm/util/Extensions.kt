package com.phinm.testfcm.util

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.phinm.testfcm.data.EventConfig
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
    val intervalMinutes = intervalMinutesStr.intervalMinutesToLong()

    val timePoints = mutableListOf<LocalTime>()
    var current = startTime

    while (true) {
        timePoints.add(current)
        val nextTime = current.plusMinutes(intervalMinutes)

        // Điều kiện dừng: Nếu thời gian đã vượt quá endTime trong cùng ngày hoặc qua ngày hôm sau
        if ((nextTime.isAfter(endTime) && startTime.isBefore(endTime)) ||
            (nextTime == endTime)
        ) {
            timePoints.add(endTime) // Thêm thời gian kết thúc vào danh sách
            break
        }

        current = nextTime

        // Xử lý trường hợp qua ngày hôm sau (nếu vượt quá 23:59, quay về 00:00)
        if (current.isBefore(startTime)) break
    }

    return timePoints.map { it.toUTCFormat() }
}