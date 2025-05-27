package hse.diploma.cybersecplatform.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import dagger.Component
import hse.diploma.cybersecplatform.MainApplication
import hse.diploma.cybersecplatform.data.api.AppPreferencesManager
import hse.diploma.cybersecplatform.data.api.TokenManager
import hse.diploma.cybersecplatform.domain.repository.AuthRepo
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        ViewModelModule::class,
        RepoModule::class,
        NetworkModule::class,
    ],
)
interface AppComponent {
    fun inject(app: MainApplication)

    fun applicationContext(): Context

    fun viewModelFactory(): ViewModelProvider.Factory

    fun tokenManager(): TokenManager

    fun appPreferencesManager(): AppPreferencesManager

    fun authRepo(): AuthRepo
}
