package hse.diploma.cybersecplatform.di.vm

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelProvider
import hse.diploma.cybersecplatform.ui.screens.auth.AuthStateViewModel

val LocalViewModelFactory =
    staticCompositionLocalOf<ViewModelProvider.Factory> {
        error("ViewModelFactory isn't found.")
    }

val LocalAuthStateViewModel =
    staticCompositionLocalOf<AuthStateViewModel> {
        error("No AuthStateViewModel provided")
    }
