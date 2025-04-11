package hse.diploma.cybersecplatform.ui.base

import androidx.lifecycle.ViewModelProvider
import hse.diploma.cybersecplatform.di.ActivityComponent

interface LifecycleComponentActivity {
    val viewModelFactory: ViewModelProvider.Factory
    val activityComponent: ActivityComponent
}
