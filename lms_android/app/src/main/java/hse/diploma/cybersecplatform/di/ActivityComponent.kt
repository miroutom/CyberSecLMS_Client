package hse.diploma.cybersecplatform.di

import dagger.Component
import hse.diploma.cybersecplatform.MainActivity
import hse.diploma.cybersecplatform.ui.screens.auth.AuthStateViewModel
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope

@ActivityScope
@Component(
    dependencies = [
        AppComponent::class,
    ],
    modules = [
        ActivityModule::class,
    ],
)
interface ActivityComponent {
    fun inject(mainActivity: MainActivity)

    fun authStateViewModel(): AuthStateViewModel
}
