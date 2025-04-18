package hse.diploma.cybersecplatform.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import dagger.Component
import hse.diploma.cybersecplatform.MainApplication
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        ViewModelModule::class,
        RepoModule::class,
    ],
)
interface AppComponent {
    fun inject(app: MainApplication)

    fun applicationContext(): Context

    fun viewModelFactory(): ViewModelProvider.Factory
}
