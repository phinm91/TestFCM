package com.phinm.testfcm.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.phinm.testfcm.ui.view.ListEventsDestination
import com.phinm.testfcm.ui.view.ListEventsScreen
import com.phinm.testfcm.ui.view.NewEventDestination
import com.phinm.testfcm.ui.view.NewEventScreen

@Composable
fun NavigationHost(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = ListEventsDestination.route,
        modifier = modifier,
    ) {
        composable(route = ListEventsDestination.route) {
            ListEventsScreen(navHostController = navController)
        }
        composable(route = NewEventDestination.route) {
            NewEventScreen(navHostController = navController)
        }
    }
}