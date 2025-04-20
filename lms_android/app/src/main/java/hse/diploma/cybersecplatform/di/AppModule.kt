package hse.diploma.cybersecplatform.di

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import dagger.Module
import dagger.Provides
import hse.diploma.cybersecplatform.data.api.AppPreferencesManager
import hse.diploma.cybersecplatform.data.api.TokenManager
import javax.inject.Singleton

@Module
class AppModule(
    private val application: Application,
) {
    @Singleton
    @Provides
    fun provideApplicationContext(): Context = application.applicationContext

    @Provides
    @Singleton
    fun providePackageManager(context: Context): PackageManager = context.packageManager

    @Provides
    @Singleton
    fun provideAppPreferencesManager(context: Context): AppPreferencesManager {
        return AppPreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideTokenManager(context: Context): TokenManager {
        return TokenManager(context)
    }
}
