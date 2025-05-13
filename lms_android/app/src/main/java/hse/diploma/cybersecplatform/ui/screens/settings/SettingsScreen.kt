package hse.diploma.cybersecplatform.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: SettingsScreenViewModel = viewModel(factory = LocalViewModelFactory.current)

    val locale by viewModel.locale.collectAsState()
    val updateProfileState by viewModel.updateProfileState.collectAsState()
    val updatePasswordState by viewModel.updatePasswordState.collectAsState()
}
