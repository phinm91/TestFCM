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
        //Item đã bị xoá.
        navController.popBackStack()
    }
    EventBody(
        modifier = modifier,
        eventConfig =  event
    ) {
        scope.launch {
            editEventViewModel.updateEvent(eventConfig = it)
            navController.popBackStack()
        }
    }
}