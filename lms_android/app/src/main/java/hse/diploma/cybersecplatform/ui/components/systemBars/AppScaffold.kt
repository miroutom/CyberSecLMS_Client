package hse.diploma.cybersecplatform.ui.components.systemBars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController

@Composable
fun AppScaffold(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopBar(navController)
        },
        bottomBar = {
            CustomNavigationBar(navController)
        },
        content = { paddingValues ->
            Column(
                modifier =
                    modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(Color.White),
            ) {
                content(paddingValues)
            }
        },
    )
}
