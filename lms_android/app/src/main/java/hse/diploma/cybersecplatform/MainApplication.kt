package hse.diploma.cybersecplatform

import android.app.Application
import android.content.Context
import hse.diploma.cybersecplatform.data.api.AppPreferencesManager
import hse.diploma.cybersecplatform.di.AppComponent
import hse.diploma.cybersecplatform.di.AppModule
import hse.diploma.cybersecplatform.di.DaggerAppComponent
import hse.diploma.cybersecplatform.domain.model.Language
import javax.inject.Inject

class MainApplication : Application() {
    @Inject lateinit var appPreferencesManager: AppPreferencesManager

    override fun onCreate() {
        super.onCreate()
        appComponent =
            DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
        appComponent.inject(this)

        val prefs = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val langOrdinal = prefs.getInt("app_language", Language.ENGLISH.ordinal)
        appLocale = Language.entries.toTypedArray().getOrElse(langOrdinal) { Language.ENGLISH }
    }

    companion object {
        lateinit var appComponent: AppComponent private set
        lateinit var appLocale: Language private set
    }
}
