package com.phinm.testfcm.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.phinm.testfcm.MainApplication
import com.phinm.testfcm.data.EventConfig
import com.phinm.testfcm.ui.navigation.NavigationDestination
import com.phinm.testfcm.viewmodel.AppViewModelProvider
import kotlinx.coroutines.launch

object ListEventsDestination : NavigationDestination {
    override val route = "main"
    override val titleRes = -1
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListEventsScreen(
    modifier: Modifier = Modifier,
    listEventsViewModel: ListEventsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navHostController: NavHostController
) {
    var fcmTokenIsValid by remember { mutableStateOf(false) }
    var signInIsValid by remember { mutableStateOf(false) }
    val uiStateIsValid by remember {
        derivedStateOf { fcmTokenIsValid && signInIsValid }
    }
    LaunchedEffect(Unit) {
        MainApplication.initFCMToken { result ->
            fcmTokenIsValid = result
        }
        MainApplication.signInAnonymously { result ->
            signInIsValid = result
        }
    }
    val coroutineScope = rememberCoroutineScope()
    if (!uiStateIsValid) {
        UserInit(modifier)
        return
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text("List Event") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navHostController.navigate(route = NewEventDestination.route)
                },
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
    ) { innerPadding ->
        val events by listEventsViewModel.events.collectAsStateWithLifecycle()
        ListEvents(
            modifier = Modifier.padding(innerPadding),
            events,
            onDelete = {
                coroutineScope.launch {
                    listEventsViewModel.deleteEvent(it)
                }
            },
            onEdit = {
                navHostController.navigate(route = "${EditEventDestination.route}/${it.id}")
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInit(
    modifier: Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text("Đang xử lý") })
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Đang khởi tạo FCM Token, User ID...")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FCMInitPreview() {
    UserInit(
        modifier = Modifier.background(Color.White)
    )
}

@Composable
fun ListEvents(
    modifier: Modifier,
    eventConfigs: List<EventConfig>,
    onEdit: (EventConfig) -> Unit,
    onDelete: (EventConfig) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(eventConfigs) { event ->
            EventBody(
                event = event,
                onEdit = onEdit,
                onDelete = onDelete
            )
        }
    }
}

@Composable
@Preview
fun ListReminderPreview() {
    ListEvents(
        modifier = Modifier.background(Color.White),
        eventConfigs = listOf(
            EventConfig(
                "1", "Ovulation", "Description 1",
                "2025-02-28", "02:00Z"
            ),
            EventConfig(
                "2", "Sleep", "Description 2", "",
                firstNotifyTime = "16:00Z",
                lastNotifyTime = "16:00Z",
                notificationInterval = "1d"
            ),
            EventConfig(
                "2", "Drink water", "Description 3", "",
                firstNotifyTime = "03:00Z",
                lastNotifyTime = "10:00Z",
                notificationInterval = "90m"
            ),
        ),
        onDelete = {},
        onEdit = {},
    )
}