package hse.diploma.cybersecplatform.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.components.buttons.CustomOutlinedButton
import hse.diploma.cybersecplatform.ui.components.buttons.CustomStepper
import hse.diploma.cybersecplatform.ui.components.buttons.FilledButton
import hse.diploma.cybersecplatform.ui.components.buttons.SkipButton
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnBoardingScreen(
    onNavigateToAuthorization: () -> Unit,
    onNavigateToRegistration: () -> Unit,
    viewModel: OnBoardingScreenViewModel = viewModel(),
    modifier: Modifier = Modifier,
) {
    val currentStep by viewModel.currentPage.collectAsState()
    val pagerState = rememberPagerState(initialPage = currentStep)

    LaunchedEffect(currentStep) {
        if (pagerState.currentPage != currentStep) {
            pagerState.animateScrollToPage(currentStep)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (page != currentStep) {
                viewModel.setPage(page)
            }
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.White),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CustomStepper(
                    currentStep = currentStep,
                    totalSteps = 3,
                    modifier = Modifier.weight(1f),
                )
                if (!isLastStep(currentStep)) {
                    SkipButton(onClick = onNavigateToAuthorization)
                } else {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            HorizontalPager(
                count = 3,
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Text(
                        text = currentStepToText(page).first,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 28.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        text = currentStepToText(page).second,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = colorResource(R.color.supporting_text),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Image(
                        painter = currentStepToImage(page),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),
                    )
                }
            }

            FilledButton(
                text = currentStepToButtonText(currentStep),
                onClick = {
                    if (isLastStep(currentStep)) {
                        onNavigateToRegistration()
                    } else {
                        viewModel.onNextPage()
                    }
                },
            )

            if (isLastStep(currentStep)) {
                CustomOutlinedButton(
                    text = stringResource(R.string.auth_button),
                    onClick = onNavigateToAuthorization,
                )
            }
        }
    }
}

private fun isLastStep(step: Int) = step == 2

@Composable
private fun currentStepToText(step: Int): Pair<String, String> {
    return when (step) {
        0 -> stringResource(R.string.onboarding_title_first) to stringResource(R.string.onboarding_label_first)
        1 -> stringResource(R.string.onboarding_title_second) to stringResource(R.string.onboarding_label_second)
        else -> stringResource(R.string.onboarding_title_third) to stringResource(R.string.onboarding_label_third)
    }
}

@Composable
private fun currentStepToImage(step: Int): Painter {
    return when (step) {
        0 -> painterResource(R.drawable.onboarding_first)
        1 -> painterResource(R.drawable.onboarding_second)
        else -> painterResource(R.drawable.onboarding_third)
    }
}

@Composable
private fun currentStepToButtonText(step: Int): String {
    return when (step) {
        0, 1 -> stringResource(R.string.onboarding_next_page_button)
        else -> stringResource(R.string.register_button)
    }
}

@Preview
@Composable
fun OnBoardingScreenPreview() {
    val mockViewModel =
        object : OnBoardingScreenViewModel() {
            override val currentPage: StateFlow<Int>
                get() = MutableStateFlow(0)
        }

    OnBoardingScreen(
        onNavigateToAuthorization = {},
        onNavigateToRegistration = {},
        viewModel = mockViewModel,
    )
}
