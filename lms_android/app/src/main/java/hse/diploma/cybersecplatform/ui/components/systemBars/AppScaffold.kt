package hse.diploma.cybersecplatform.ui.components.systemBars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import hse.diploma.cybersecplatform.utils.logD

private const val TAG = "AppScaffold"

@Composable
fun AppScaffold(
    navController: NavHostController,
    content: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        topBar = {
            TopBar(navController)
        },
        bottomBar = {
            CustomNavigationBar(navController)
        },
        content = { paddingValues ->
            logD(
                TAG,
                "paddingValues " +
                    "top = ${paddingValues.calculateTopPadding()}, " +
                    "bottom = ${paddingValues.calculateBottomPadding()}",
            )
            content(
                Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding(),
                    )
                    .fillMaxSize()
                    .background(Color.White),
            )
        },
    )
}
