package com.example.agceasydoc.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agceasydoc.MyApplication
import com.example.agceasydoc.Screen
import com.example.agceasydoc.data.DocumentViewModel
import com.example.agceasydoc.onbording.SplashScreen
import com.example.agceasydoc.screens.CustomizeDocScreen
import com.example.agceasydoc.screens.HomeScreen
import com.example.agceasydoc.screens.RecentDocumentsScreen
import com.example.agceasydoc.screens.SettingsScreen

@Composable
fun AppNav() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val application = context.applicationContext as MyApplication

    val sharedViewModel: DocumentViewModel = viewModel(viewModelStoreOwner = application)

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                viewModel = sharedViewModel,
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.CustomizeDoc.route) {
            CustomizeDocScreen(sharedViewModel)
        }

        composable(Screen.RecentDocuments.route) {
            RecentDocumentsScreen(navController, sharedViewModel)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
