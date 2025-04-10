package hse.diploma.cybersecplatform.di

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.pm.PackageManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(
    private val application: Application,
) {
    @Singleton
    @Provides
    fun provideApplicationContext(): Context = application.applicationContext

    @Singleton
    @Provides
    fun provideAppWidgetManager(context: Context): AppWidgetManager = AppWidgetManager.getInstance(context)

    @Provides
    @Singleton
    fun providePackageManager(context: Context): PackageManager = context.packageManager
}
