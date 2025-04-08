package hse.diploma.cybersecplatform

import android.app.Application
import hse.diploma.cybersecplatform.di.AppComponent
import hse.diploma.cybersecplatform.di.AppModule
import hse.diploma.cybersecplatform.di.DaggerAppComponent

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appComponent =
            DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
        appComponent.inject(this)
    }

    companion object {
        lateinit var appComponent: AppComponent private set
    }
}
