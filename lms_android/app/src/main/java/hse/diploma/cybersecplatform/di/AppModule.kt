package hse.diploma.cybersecplatform.di

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import dagger.Module
import dagger.Provides
import hse.diploma.cybersecplatform.data.repo.VulnerabilityRepoImpl
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
    fun provideVulnerabilityRepository(context: Context): VulnerabilityRepoImpl {
        return VulnerabilityRepoImpl(context)
    }
}
