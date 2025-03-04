package com.phinm.testfcm.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.phinm.testfcm.data.EventConfig
import com.phinm.testfcm.data.EventConfig.Companion.isRepeatedDaily
import com.phinm.testfcm.data.EventConfig.Companion.isRepeatedMultiTimePerDay
import com.phinm.testfcm.util.fromUTCTimeToLocaleTime

@Composable
fun EventBody(
    event: EventConfig,
    onEdit: (EventConfig) -> Unit = {},
    onDelete: (EventConfig) -> Unit = {},
) {
    Row(
        modifier = Modifier.padding(8.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.bodyLarge,
            )

            Text(
                text = event.description,
                style = MaterialTheme.typography.labelSmall,
            )
            val notifyTimeContent: String =
                if (event.notificationInterval.isRepeatedDaily()) {
                    "Every day at ${event.firstNotifyTime.fromUTCTimeToLocaleTime()}"
                } else if (event.notificationInterval.isRepeatedMultiTimePerDay()) {
                    "Every day from ${event.firstNotifyTime.fromUTCTimeToLocaleTime()} to ${event.lastNotifyTime.fromUTCTimeToLocaleTime()} every ${event.notificationInterval}"
                } else {
                    "At ${event.notifyDate} ${event.firstNotifyTime.fromUTCTimeToLocaleTime()}"
                }
            Text(
                text = notifyTimeContent,
                style = MaterialTheme.typography.labelSmall,
            )
            HorizontalDivider()
        }
        IconButton(
            modifier = Modifier.padding(8.dp),
            onClick = {
                onEdit(event)
            }) {
            Icon(
                Icons.Default.Edit,
                tint = Color.Black,
                contentDescription = null
            )
        }
        IconButton(
            modifier = Modifier.padding(8.dp),
            onClick = {
                onDelete(event)
            }) {
            Icon(
                Icons.Default.Delete,
                tint = Color.Black,
                contentDescription = null
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun OvulationPreview() {
    EventBody(
        event = EventConfig(
            title = "Ovulation",
            description = "Ovulation description",
            notifyDate = "2025-03-10",
            firstNotifyTime = "02:00Z",
            lastNotifyTime = "02:00Z",
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun SleepPreview() {
    EventBody(
        event = EventConfig(
            title = "Sleep",
            description = "Sleep description",
            notifyDate = "",
            firstNotifyTime = "16:00Z",
            lastNotifyTime = "16:00Z",
            notificationInterval = "1d",
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun DrinkWaterPreview() {
    EventBody(
        event = EventConfig(
            title = "Drink Water",
            description = "Drink Water description",
            notifyDate = "",
            firstNotifyTime = "02:00Z",
            lastNotifyTime = "09:00Z",
            notificationInterval = "90m",
        )
    )
}