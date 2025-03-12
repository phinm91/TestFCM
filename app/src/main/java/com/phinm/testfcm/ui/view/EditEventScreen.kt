package com.phinm.testfcm.ui.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.phinm.testfcm.data.EventConfig
import com.phinm.testfcm.ui.navigation.NavigationDestination
import com.phinm.testfcm.viewmodel.AppViewModelProvider
import kotlinx.coroutines.launch
import timber.log.Timber

object EditEventDestination: NavigationDestination {
    override val route = "edit_event"
    override val titleRes = -1
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    editEventViewModel: EditEventViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val scope = rememberCoroutineScope()
    val event: EventConfig? by editEventViewModel.event.collectAsStateWithLifecycle()
    if (event == null) {
        Timber.v("Event not found (maybe deleted)")
        navController.popBackStack()
        return
    }
    val eventConfig = event!!
    if (eventConfig.id.isBlank()) {
        Timber.v("Event is loading")
        return
    }
    Timber.v("Edit event:\n$eventConfig")
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Event") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        EventEditorBody(
            modifier = modifier.padding(innerPadding),
            eventConfig = eventConfig
        ) {
            scope.launch {
                editEventViewModel.updateEvent(eventConfig = it)
                navController.popBackStack()
            }
        }
    }
}