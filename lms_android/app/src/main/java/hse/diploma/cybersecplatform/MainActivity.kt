package hse.diploma.cybersecplatform

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hse.diploma.cybersecplatform.data.api.AppPreferencesManager
import hse.diploma.cybersecplatform.di.ActivityComponent
import hse.diploma.cybersecplatform.di.ActivityModule
import hse.diploma.cybersecplatform.di.DaggerActivityComponent
import hse.diploma.cybersecplatform.di.vm.LocalAuthStateViewModel
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory
import hse.diploma.cybersecplatform.domain.model.AppTheme
import hse.diploma.cybersecplatform.domain.model.Language
import hse.diploma.cybersecplatform.navigation.MainNavigationGraph
import hse.diploma.cybersecplatform.navigation.authNavigationGraph
import hse.diploma.cybersecplatform.ui.base.LifecycleComponentActivity
import hse.diploma.cybersecplatform.ui.screens.SplashScreen
import hse.diploma.cybersecplatform.ui.screens.auth.AuthStateViewModel
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.utils.logD
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

class MainActivity : ComponentActivity(), LifecycleComponentActivity {
    private var activityComponentInternal: ActivityComponent? = null
    override val activityComponent: ActivityComponent
        get() = activityComponentInternal!!

    @Inject lateinit var viewModelFactoryInternal: ViewModelProvider.Factory
    override val viewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactoryInternal

    @Inject lateinit var authStateViewModel: AuthStateViewModel

    private lateinit var appPreferencesManager: AppPreferencesManager

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val langOrdinal = prefs.getInt("app_language", Language.ENGLISH.ordinal)
        val language = Language.entries.toTypedArray().getOrElse(langOrdinal) { Language.ENGLISH }

        val wrappedContext = newBase.wrapWithLocale(language.toLocale())
        super.attachBaseContext(wrappedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        logD(TAG, "onCreate")
        initDaggerComponent()
        super.onCreate(savedInstanceState)

        appPreferencesManager = (application as MainApplication).appPreferencesManager

        observePreferences()
        setupUI()
    }

    private fun initDaggerComponent() {
        activityComponentInternal =
            DaggerActivityComponent.builder()
                .appComponent(MainApplication.appComponent)
                .activityModule(ActivityModule(this))
                .build()
        activityComponent.inject(this)
    }

    private fun observePreferences() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                appPreferencesManager.languageFlow
                    .drop(1)
                    .distinctUntilChanged()
                    .collect { _ ->
                        restartActivityForConfigurationChange()
                    }
            }
        }
    }

    private fun restartActivityForConfigurationChange() {
        val intent =
            Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        finish()
        startActivity(intent)
    }

    private fun setupUI() {
        setContent {
            val currentAppTheme by appPreferencesManager.themeFlow.collectAsState()

            val useDarkTheme =
                when (currentAppTheme) {
                    AppTheme.LIGHT -> false
                    AppTheme.DARK -> true
                    AppTheme.SYSTEM -> isSystemInDarkTheme()
                }

            updateConfiguration(useDarkTheme)

            CompositionLocalProvider(
                LocalViewModelFactory provides viewModelFactory,
                LocalAuthStateViewModel provides authStateViewModel,
            ) {
                CyberSecPlatformTheme(darkTheme = useDarkTheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        val navController = rememberNavController()
                        val isAuthorized by authStateViewModel.isAuthorized.collectAsState()
                        NavHost(
                            navController = navController,
                            startDestination = "splash",
                        ) {
                            composable("splash") {
                                SplashScreen(
                                    isAuthorized = isAuthorized,
                                    onSplashCompleted = { isAuth ->
                                        navController.navigate(if (isAuth) "main_flow" else "auth_flow") {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    },
                                )
                            }
                            authNavigationGraph(
                                navController = navController,
                                onAuthCompleted = {
                                    authStateViewModel.authorize()
                                    navController.navigate("main_flow") {
                                        popUpTo("auth_flow") { inclusive = true }
                                    }
                                },
                                appPreferencesManager = appPreferencesManager,
                            )
                            composable("main_flow") {
                                MainNavigationGraph()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateConfiguration(isNightTheme: Boolean) {
        this.resources.configuration.uiMode =
            if (isNightTheme) {
                Configuration.UI_MODE_NIGHT_YES
            } else {
                Configuration.UI_MODE_NIGHT_NO
            }
        this.resources.updateConfiguration(this.resources.configuration, this.resources.displayMetrics)
    }

    private fun Context.wrapWithLocale(locale: Locale): Context {
        val currentConfig = resources.configuration
        val newConfig = Configuration(currentConfig)
        newConfig.setLocale(locale)
        return createConfigurationContext(newConfig)
    }

    private fun Language.toLocale(): Locale =
        when (this) {
            Language.RUSSIAN -> Locale("ru")
            else -> Locale("en")
        }

    companion object {
        private const val TAG = "MainActivity"
    }
}
