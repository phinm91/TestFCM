package com.phinm.testfcm.ui.custom

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.phinm.testfcm.util.fromDateToString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePicker(
    date: String,
    format: String,
    onPicked: (String) -> Unit = {}
) {
    val showDatePicker = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Button(
        modifier = Modifier.padding(horizontal = 8.dp),
        onClick = { showDatePicker.value = true },
    ) {
        Text(text = String.format(format, date))
    }

    if (showDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker.value = false
                        onPicked(datePickerState.selectedDateMillis?.fromDateToString() ?: "")
                    }
                ) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDatePicker() {
    MyDatePicker(
        date = "2025-03-01",
        format = "Ngày %s",
    )
}