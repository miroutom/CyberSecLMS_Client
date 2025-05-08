package hse.diploma.cybersecplatform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hse.diploma.cybersecplatform.data.api.AppPreferencesManager
import hse.diploma.cybersecplatform.data.api.TokenManager
import hse.diploma.cybersecplatform.di.ActivityComponent
import hse.diploma.cybersecplatform.di.ActivityModule
import hse.diploma.cybersecplatform.di.DaggerActivityComponent
import hse.diploma.cybersecplatform.di.vm.LocalAuthStateViewModel
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory
import hse.diploma.cybersecplatform.ui.base.LifecycleComponentActivity
import hse.diploma.cybersecplatform.ui.navigation.MainNavigationGraph
import hse.diploma.cybersecplatform.ui.navigation.authNavigationGraph
import hse.diploma.cybersecplatform.ui.screens.auth.AuthStateViewModel
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.utils.logD
import javax.inject.Inject

class MainActivity : ComponentActivity(), LifecycleComponentActivity {
    private var activityComponentInternal: ActivityComponent? = null

    override val activityComponent: ActivityComponent
        get() = activityComponentInternal!!

    @Inject
    lateinit var viewModelFactoryInternal: ViewModelProvider.Factory

    override val viewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactoryInternal

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var appPreferencesManager: AppPreferencesManager

    @Inject
    lateinit var authStateViewModel: AuthStateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        logD(TAG, "onCreate")

        initActivityDaggerComponent()
        super.onCreate(savedInstanceState)

        setContent {
            CyberSecPlatformTheme {
                CompositionLocalProvider(
                    LocalViewModelFactory provides viewModelFactory,
                    LocalAuthStateViewModel provides authStateViewModel,
                ) {
                    val navController = rememberNavController()
                    navController.setViewModelStore(viewModelStore)

                    val isAuthorized by authStateViewModel.isAuthorized.collectAsState()

                    NavHost(
                        navController = navController,
                        startDestination = if (isAuthorized) "main_flow" else "auth_flow",
                    ) {
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

    private fun initActivityDaggerComponent() {
        activityComponentInternal =
            DaggerActivityComponent.builder()
                .appComponent(MainApplication.appComponent)
                .activityModule(ActivityModule(this))
                .build()
        activityComponent.inject(this)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
