package hse.diploma.cybersecplatform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import hse.diploma.cybersecplatform.ui.navigation.AuthNavigationGraph
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CyberSecPlatformTheme {
                val navController = rememberNavController()
                AuthNavigationGraph(navController = navController)
            }
        }
    }
}
