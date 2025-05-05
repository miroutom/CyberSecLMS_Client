package hse.diploma.cybersecplatform.di.vm

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelProvider

val LocalViewModelFactory =
    staticCompositionLocalOf<ViewModelProvider.Factory> {
        error("ViewModelFactory isn't found.")
    }
