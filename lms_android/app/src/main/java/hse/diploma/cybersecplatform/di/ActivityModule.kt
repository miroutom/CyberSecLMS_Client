package hse.diploma.cybersecplatform.di

import android.app.Activity
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(
    private val activity: Activity,
) {
    @Provides
    fun provideActivityContext(): Context = activity

    @Provides
    fun provideActivity(): Activity = activity
}
