package hse.diploma.cybersecplatform.ui.components.systemBars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.screens.profile.ProfileViewModel

@Composable
fun AppScaffold(
    profileViewModel: ProfileViewModel,
    navController: NavHostController,
    content: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        topBar = {
            TopBar(profileViewModel, navController)
        },
        bottomBar = {
            CustomNavigationBar(navController)
        },
        content = { paddingValues ->
            content(
                Modifier
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding(),
                    )
                    .fillMaxSize()
                    .background(colorResource(R.color.background)),
            )
        },
    )
}
