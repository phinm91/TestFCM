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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.phinm.testfcm.ui.navigation.NavigationDestination
import com.phinm.testfcm.viewmodel.AppViewModelProvider
import kotlinx.coroutines.launch

object NewEventDestination : NavigationDestination {
    override val route = "new_event"
    override val titleRes = -1
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEventScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    newEventViewModel: NewEventViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Event") },
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
            modifier = modifier.padding(innerPadding)
        ) {
            scope.launch {
                newEventViewModel.addEvent(eventConfig = it)
                navController.popBackStack()
            }
        }
    }
}