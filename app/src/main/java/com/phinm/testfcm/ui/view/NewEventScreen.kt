package com.phinm.testfcm.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.phinm.testfcm.ui.navigation.NavigationDestination
import com.phinm.testfcm.viewmodel.AppViewModelProvider
import kotlinx.coroutines.launch

object NewEventDestination: NavigationDestination {
    override val route = "new_event"
    override val titleRes = -1
}

@Composable
fun NewEventScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    newEventViewModel: NewEventViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val scope = rememberCoroutineScope()
    EventEditorBody(
        modifier = modifier
    ) {
        scope.launch {
            newEventViewModel.addEvent(eventConfig = it)
            navController.popBackStack()
        }
    }
}