package hse.diploma.cybersecplatform.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import hse.diploma.cybersecplatform.di.vm.ViewModelFactory
import hse.diploma.cybersecplatform.di.vm.ViewModelKey
import hse.diploma.cybersecplatform.ui.screens.auth.AuthStateViewModel
import hse.diploma.cybersecplatform.ui.screens.auth.AuthorizationViewModel
import hse.diploma.cybersecplatform.ui.screens.auth.RegistrationViewModel
import hse.diploma.cybersecplatform.ui.screens.code_editor.CodeEditorViewModel
import hse.diploma.cybersecplatform.ui.screens.courses.MyCoursesViewModel
import hse.diploma.cybersecplatform.ui.screens.home.HomeViewModel
import hse.diploma.cybersecplatform.ui.screens.onboarding.OnBoardingViewModel
import hse.diploma.cybersecplatform.ui.screens.otp.OtpViewModel
import hse.diploma.cybersecplatform.ui.screens.profile.ProfileViewModel
import hse.diploma.cybersecplatform.ui.screens.settings.SettingsViewModel
import hse.diploma.cybersecplatform.ui.screens.tasks.TasksViewModel
import hse.diploma.cybersecplatform.ui.screens.teacher.TeacherCoursesViewModel

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
    @ViewModelKey(HomeViewModel::class)
    abstract fun provideHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TasksViewModel::class)
    abstract fun provideTasksViewModel(viewModel: TasksViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthorizationViewModel::class)
    abstract fun provideAuthorizationViewModel(viewModel: AuthorizationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    abstract fun provideRegistrationViewModel(viewModel: RegistrationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun provideProfileViewModel(viewModel: ProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyCoursesViewModel::class)
    abstract fun provideMyCoursesViewModel(viewModel: MyCoursesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun provideSettingsViewModel(viewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TeacherCoursesViewModel::class)
    abstract fun provideTeacherCoursesViewModel(viewModel: TeacherCoursesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OnBoardingViewModel::class)
    abstract fun provideOnBoardingViewModel(viewModel: OnBoardingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CodeEditorViewModel::class)
    abstract fun provideCodeEditorViewModel(viewModel: CodeEditorViewModel): ViewModel
}
