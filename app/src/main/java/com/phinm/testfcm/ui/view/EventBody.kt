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
import com.phinm.testfcm.ui.custom.MyDatePicker
import com.phinm.testfcm.ui.custom.Spinner

@Composable
fun EventBody(
    modifier: Modifier = Modifier,
    eventConfig: EventConfig? = null,

    onDone: (EventConfig) -> Unit = {},
) {
    var title by remember { mutableStateOf(eventConfig?.title ?: "") }
    var description by remember { mutableStateOf(eventConfig?.description ?: "") }
    var repeatable by remember { mutableStateOf(eventConfig?.isRepeatedDaily() ?: false) }
    var date by remember { mutableStateOf(eventConfig?.notifyDate ?: "") }
    val firstNotifyTime by remember { mutableStateOf(eventConfig?.firstNotifyTime ?: "09:00") }
    val lastNotifyTime by remember { mutableStateOf(eventConfig?.lastNotifyTime ?: "21:00") }
    var notifyInterval by remember {
        mutableStateOf(
            eventConfig?.notificationInterval
                ?: EventConfig.NOTIFICATION_INTERVAL_FORMAT_DAILY
        )
    }

    Column(
        modifier = modifier,
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
        )
        TextField(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
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
            if (notifyInterval == EventConfig.NOTIFICATION_INTERVAL_FORMAT_DAILY) {
                //Lặp lại hàng ngày, mỗi ngày 1 lần.
                Button(onClick = {

                }) {
                    Text("Giờ $firstNotifyTime")
                }
            } else {
                //Lặp lại hàng ngày, mỗi ngày nhiều lần.
                Row {
                    Button(onClick = {

                    }) {
                        Text("Giờ bắt đầu $firstNotifyTime")
                    }
                    Button(onClick = {

                    }) {
                        Text("Giờ kết thúc $lastNotifyTime")
                    }
                }

            }
        } else {
            //Không lặp lại, sự kiện 1 lần duy nhất.
            Row {
                MyDatePicker(
                    date = date,
                    format = "Ngày %s",
                    onPicked = { date = it}
                )

                Button(onClick = {

                }) {
                    Text("Giờ : $firstNotifyTime")
                }
            }
        }

        Row(
            modifier = Modifier.padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                //TODO : Tạo item EventConfig từ dữ liệu đang có.
                val result = eventConfig?.copy() ?: EventConfig()
                result.apply {
                    this.title = title
                    this.description = description
                    this.notifyDate = date
                    this.firstNotifyTime = firstNotifyTime
                    this.lastNotifyTime = lastNotifyTime
                    this.notificationInterval = notifyInterval
                }
                onDone(result)
            }) {
                Text("Done")
            }
        }
    }
}

@Preview("Tạo mới", showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun EventBodyPreview() {
    EventBody()
}

@Preview(
    name = "Không lặp lại",
    showBackground = true, backgroundColor = 0xFFFFFF
)
@Composable
fun EventBodyEdit1Preview() {
    EventBody(
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

@Preview(
    name = "Lặp lại 1 lần trong ngày",
    showBackground = true, backgroundColor = 0xFFFFFF
)
@Composable
fun EventBodyEdit2Preview() {
    EventBody(
        eventConfig = EventConfig(
            title = "Nhắc nhở ghi cân nặng",
            description = "Hãy ghi lại cân nặng hôm nay của bạn",
            notifyDate = "",
            firstNotifyTime = "02:00Z",
            lastNotifyTime = "",
            notificationInterval = "1d"
        )
    )
}

@Preview(
    name = "Lặp lại vài lần trong ngày",
    showBackground = true, backgroundColor = 0xFFFFFF
)
@Composable
fun EventBodyEdit3Preview() {
    EventBody(
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