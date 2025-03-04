package com.phinm.testfcm.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.phinm.testfcm.data.EventConfig
import com.phinm.testfcm.data.EventConfig.Companion.isRepeated
import com.phinm.testfcm.data.EventConfig.Companion.isRepeatedDaily
import com.phinm.testfcm.data.EventConfig.Companion.isRepeatedMultiTimePerDay
import com.phinm.testfcm.ui.custom.MyDatePicker
import com.phinm.testfcm.ui.custom.MyTimePicker
import com.phinm.testfcm.ui.custom.Spinner
import com.phinm.testfcm.util.fromLocalTimeToString
import com.phinm.testfcm.util.fromLocalTimeToUTCTime
import com.phinm.testfcm.util.fromUTCTimeToLocaleTime
import timber.log.Timber
import java.time.LocalDate
import java.util.Locale
import java.util.UUID

@Composable
fun EventEditorBody(
    modifier: Modifier = Modifier,
    eventConfig: EventConfig? = null,
    onDone: (EventConfig) -> Unit = {},
) {
    var title by remember { mutableStateOf(eventConfig?.title ?: "") }
    var description by remember { mutableStateOf(eventConfig?.description ?: "") }
    var repeatable by remember { mutableStateOf(eventConfig?.isRepeated() ?: false) }
    var date by remember { mutableStateOf(eventConfig?.notifyDate ?: LocalDate.now().toString()) }
    var firstNotifyTime by remember {
        mutableStateOf(
            eventConfig?.firstNotifyTime?.fromUTCTimeToLocaleTime() ?: "09:00"
        )
    }
    var lastNotifyTime by remember {
        mutableStateOf(eventConfig?.lastNotifyTime?.fromUTCTimeToLocaleTime() ?: "17:00")
    }
    var notifyInterval by remember {
        mutableStateOf(eventConfig?.notificationInterval ?: "1d")
    }

    Column(
        modifier = modifier,
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = repeatable,
                onCheckedChange = {
                    repeatable = it
                }
            )
            Text(
                text = "Lặp lại hàng ngày",
                textAlign = TextAlign.Center,
            )
        }
        if (repeatable) {
            Spinner(
                title = "Khoảng thời gian :",
                options = listOf("1d", "30m", "60m", "90m"),
                selectedOption = notifyInterval
            ) {
                notifyInterval = it
            }
            if (notifyInterval.isRepeatedDaily()) {
                //Lặp lại hàng ngày, mỗi ngày 1 lần.
                MyTimePicker(
                    modifier = Modifier.padding(start = 8.dp),
                    hour = firstNotifyTime.split(":")[0].toInt(),
                    minute = firstNotifyTime.split(":")[1].toInt(),
                    format = "Giờ %02d:%02d",
                    onTimeSelected = { hour, minute ->
                        firstNotifyTime = fromLocalTimeToString(hour, minute)
                    }
                )
            } else {
                //Lặp lại hàng ngày, mỗi ngày nhiều lần.
                Row {
                    MyTimePicker(
                        modifier = Modifier.padding(start = 8.dp),
                        hour = firstNotifyTime.split(":")[0].toInt(),
                        minute = firstNotifyTime.split(":")[1].toInt(),
                        format = "Giờ bắt đầu %02d:%02d",
                        onTimeSelected = { hour, minute ->
                            firstNotifyTime = fromLocalTimeToString(hour, minute)
                        }
                    )

                    MyTimePicker(
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                        hour = lastNotifyTime.split(":")[0].toInt(),
                        minute = lastNotifyTime.split(":")[1].toInt(),
                        format = "Giờ kết thúc %02d:%02d",
                        onTimeSelected = { hour, minute ->
                            lastNotifyTime =
                                String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                        }
                    )
                }

            }
        } else {
            //Không lặp lại, sự kiện 1 lần duy nhất.
            Row {
                MyDatePicker(
                    modifier = Modifier.padding(start = 8.dp),
                    date = date,
                    format = "Ngày %s",
                    onPicked = { date = it }
                )

                MyTimePicker(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                    hour = firstNotifyTime.split(":")[0].toInt(),
                    minute = firstNotifyTime.split(":")[1].toInt(),
                    onTimeSelected = { hour, minute ->
                        firstNotifyTime =
                            String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                    }
                )
            }
        }

        Row(
            modifier = Modifier.padding(top = 16.dp, start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                val result = eventConfig?.copy() ?: EventConfig()
                result.apply {
                    if (this.id.isBlank()) this.id = UUID.randomUUID().toString()
                    this.title = title
                    this.description = description
                    this.notifyDate = if (repeatable) "" else date
                    this.firstNotifyTime = firstNotifyTime.fromLocalTimeToUTCTime()
                    this.lastNotifyTime =
                        if (notifyInterval.isRepeatedMultiTimePerDay())
                            lastNotifyTime.fromLocalTimeToUTCTime()
                        else
                            this.firstNotifyTime
                    this.notificationInterval =
                        if (repeatable)
                            notifyInterval
                        else
                            EventConfig.NOTIFICATION_INTERVAL_FORMAT_NONE
                }
                Timber.d("EventConfig :\n$result")
                onDone(result)
            }) {
                Text("Done")
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun NewEventPreview() {
    EventEditorBody()
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun EventEditNoRepeatedPreview() {
    EventEditorBody(
        eventConfig = EventConfig(
            title = "Chu kỳ kinh bắt đầu",
            description = "Hãy ghi lại ngày bắt đầu chu kỳ kinh nếu nó đã đến!",
            notifyDate = "2025-03-01",
            firstNotifyTime = "02:00Z",
            lastNotifyTime = "02:00Z",
            notificationInterval = "NONE"
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun EventEditorDailyRepeatedPreview() {
    EventEditorBody(
        eventConfig = EventConfig(
            title = "Nhắc nhở ghi cân nặng",
            description = "Hãy ghi lại cân nặng hôm nay của bạn",
            notifyDate = "",
            firstNotifyTime = "02:00Z",
            lastNotifyTime = "02:00Z",
            notificationInterval = "1d"
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun EventEditorRepeatMultiTimePerDayPreview() {
    EventEditorBody(
        eventConfig = EventConfig(
            title = "Nhắc nhở uống nước",
            description = "Đến giờ uống nước rồi",
            notifyDate = "",
            firstNotifyTime = "02:00Z",
            lastNotifyTime = "10:00Z",
            notificationInterval = "90m"
        )
    )
}