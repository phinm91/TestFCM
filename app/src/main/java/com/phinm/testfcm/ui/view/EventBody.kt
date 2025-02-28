package com.phinm.testfcm.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.phinm.testfcm.data.EventConfig

@Composable
fun EventBody(
    modifier: Modifier = Modifier,
    eventConfig: EventConfig? = null,
    onDone: (EventConfig) -> Unit = {},
) {
    val title by remember { mutableStateOf(eventConfig?.title ?: "") }
    val description by remember { mutableStateOf(eventConfig?.description ?: "") }
    val repeatable by remember { mutableStateOf(eventConfig?.isRepeatedDaily() ?: false) }
    val date by remember { mutableStateOf(eventConfig?.notifyDate ?: "") }

    Column(
        modifier = modifier,
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = title,
            onValueChange = {},
            label = { Text("Title") },
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = description,
            onValueChange = {},
            label = { Text("Description") },
        )
        Button(onClick = {

        }) {
            Text("Ngày : $date")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {

            }
        ) {
            Text("Done")
        }
    }
}

@Preview
@Composable
fun EventBodyPreview() {
    EventBody()
}

@Preview
@Composable
fun EventBodyEdit1Preview(){
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

@Preview
@Composable
fun EventBodyEdit2Preview(){
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

@Preview
@Composable
fun EventBodyEdit3Preview(){
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