package hse.diploma.cybersecplatform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hse.diploma.cybersecplatform.ui.navigation.AuthNavigationGraph
import hse.diploma.cybersecplatform.ui.navigation.MainNavigationGraph
import hse.diploma.cybersecplatform.ui.screens.auth.AuthStateViewModel
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CyberSecPlatformTheme {
                val navController = rememberNavController()

                val authViewModel: AuthStateViewModel = viewModel()
                val isAuthorized by authViewModel.isAuthorized.collectAsState()

                NavHost(
                    navController = navController,
                    startDestination = if (isAuthorized) "main" else "auth",
                ) {
                    composable("auth") {
                        AuthNavigationGraph(
                            navController = navController,
                            onAuthCompleted = {
                                navController.navigate("main") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            },
                        )
                    }

                    composable("main") {
                        MainNavigationGraph(navController = navController)
                    }
                }
            }
        }
    }
}
