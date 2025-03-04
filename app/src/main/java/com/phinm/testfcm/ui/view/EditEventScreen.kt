package com.phinm.testfcm.ui.view

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
    EventEditorBody(
        modifier = modifier,
        eventConfig =  eventConfig
    ) {
        scope.launch {
            editEventViewModel.updateEvent(eventConfig = it)
            navController.popBackStack()
        }
    }
}