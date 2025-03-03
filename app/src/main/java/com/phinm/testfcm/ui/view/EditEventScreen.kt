package com.phinm.testfcm.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.phinm.testfcm.ui.navigation.NavigationDestination
import com.phinm.testfcm.viewmodel.AppViewModelProvider
import kotlinx.coroutines.launch

object EditEventDestination: NavigationDestination {
    override val route = "edit_event"
    override val titleRes = -1
}

@Composable
fun EditEventScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    eventViewModel: EventViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val scope = rememberCoroutineScope()
    EventBody(
        modifier = modifier
    ) {
        scope.launch {
            eventViewModel.updateEvent(eventConfig = it)
            navController.popBackStack()
        }
    }
}