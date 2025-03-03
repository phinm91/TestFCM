package com.phinm.testfcm.ui.custom

import android.app.TimePickerDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import java.util.Locale

@Composable
fun MyTimePicker(
    modifier: Modifier = Modifier,
    hour: Int, minute: Int,
    format: String = "Giờ %02d:%02d",
    onTimeSelected: (hour: Int, minute: Int) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current

    Button(modifier = modifier, onClick = {
        TimePickerDialog(context, { _, selectedHour, selectedMinute ->
            onTimeSelected(selectedHour, selectedMinute)
        }, hour, minute, true).show()
    }) {
        Text(String.format(Locale.getDefault(), format, hour, minute))
    }
}

@Preview
@Composable
fun PreviewTimePicker() {
    MyTimePicker(
        hour = 9, minute = 0
    )
}
