package com.phinm.testfcm.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.phinm.testfcm.data.EventConfig
import com.phinm.testfcm.ui.navigation.NavigationDestination
import com.phinm.testfcm.viewmodel.AppViewModelProvider
import kotlinx.coroutines.launch

object ListEventsDestination: NavigationDestination {
    override val route = "main"
    override val titleRes = -1
}

@Composable
fun ListEventsScreen(
    modifier: Modifier = Modifier,
    eventViewModel: EventViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        eventViewModel.addEvent(EventConfig.randomEvent())
                    }
                },
            ) {
                Icon(
                    Icons.Default.Add,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = null
                )
            }
        },
    ) { innerPadding ->
        val events by eventViewModel.events.collectAsStateWithLifecycle()
        ListEvents(
            modifier = Modifier.padding(innerPadding),
            events,
            onDelete = {
                coroutineScope.launch {
                    eventViewModel.deleteEvent(it)
                }
            },
            onUpdate = {
                coroutineScope.launch {
                    eventViewModel.updateEvent(
                        EventConfig.updateEvent(it)
                    )
                }
            },
        )
    }
}

@Composable
fun ListEvents(
    modifier: Modifier,
    eventConfigs: List<EventConfig>,
    onDelete: (EventConfig) -> Unit,
    onUpdate: (EventConfig) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(eventConfigs) { event ->
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
                    Text(
                        text = "At: ${event.notifyDate}T${event.firstNotifyTime}",
                        style = MaterialTheme.typography.labelSmall,
                    )
                    HorizontalDivider()
                }
                IconButton(
                    modifier = Modifier.padding(8.dp),
                    onClick = {
                        onUpdate(event)
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
    }
}

@Composable
@Preview
fun ListReminderPreview() {
    ListEvents(
        modifier = Modifier.background(Color.White),
        eventConfigs = listOf(
            EventConfig("1", "Title 1", "Description 1", "2025-02-28", "10:00Z"),
            EventConfig("2", "Title 2", "Description 2", "2025-02-27", "11:00Z"),
        ),
        onDelete = {},
        onUpdate = {},
    )
}