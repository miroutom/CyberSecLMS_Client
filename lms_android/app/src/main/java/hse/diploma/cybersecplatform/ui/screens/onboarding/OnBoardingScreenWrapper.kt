package hse.diploma.cybersecplatform.ui.screens.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import hse.diploma.cybersecplatform.di.vm.LocalViewModelFactory

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnBoardingScreenWrapper(
    onNavigateToAuthorization: () -> Unit,
    onNavigateToRegistration: () -> Unit,
    viewModel: OnBoardingViewModel = viewModel(factory = LocalViewModelFactory.current),
    modifier: Modifier = Modifier,
) {
    val currentStep by viewModel.currentPage.collectAsState()

    OnBoardingScreen(
        currentStep = currentStep,
        onNextPage = viewModel::onNextPage,
        onPageChanged = viewModel::setPage,
        onSkipClick = onNavigateToAuthorization,
        onAuthClick = onNavigateToAuthorization,
        onRegisterClick = onNavigateToRegistration,
        modifier = modifier,
    )
}
