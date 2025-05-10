package hse.diploma.cybersecplatform.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import hse.diploma.cybersecplatform.di.vm.ViewModelFactory
import hse.diploma.cybersecplatform.di.vm.ViewModelKey
import hse.diploma.cybersecplatform.ui.screens.auth.AuthStateViewModel
import hse.diploma.cybersecplatform.ui.screens.auth.AuthorizationScreenViewModel
import hse.diploma.cybersecplatform.ui.screens.auth.RegistrationScreenViewModel
import hse.diploma.cybersecplatform.ui.screens.courses.MyCoursesScreenViewModel
import hse.diploma.cybersecplatform.ui.screens.home.HomeScreenViewModel
import hse.diploma.cybersecplatform.ui.screens.otp.OtpViewModel
import hse.diploma.cybersecplatform.ui.screens.profile.ProfileScreenViewModel
import hse.diploma.cybersecplatform.ui.screens.tasks.TasksScreenViewModel

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(AuthStateViewModel::class)
    abstract fun provideAuthStateViewModel(viewModel: AuthStateViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OtpViewModel::class)
    abstract fun provideOtpViewModel(viewModel: OtpViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeScreenViewModel::class)
    abstract fun provideHomeScreenViewModel(viewModel: HomeScreenViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TasksScreenViewModel::class)
    abstract fun provideTasksScreenViewModel(viewModel: TasksScreenViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthorizationScreenViewModel::class)
    abstract fun provideAuthorizationScreenViewModel(viewModel: AuthorizationScreenViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegistrationScreenViewModel::class)
    abstract fun provideRegistrationScreenViewModel(viewModel: RegistrationScreenViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileScreenViewModel::class)
    abstract fun provideProfileScreenViewModel(viewModel: ProfileScreenViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyCoursesScreenViewModel::class)
    abstract fun provideMyCoursesScreenViewModel(viewModel: MyCoursesScreenViewModel): ViewModel
}
