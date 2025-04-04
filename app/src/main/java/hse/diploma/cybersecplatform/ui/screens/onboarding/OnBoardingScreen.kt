package hse.diploma.cybersecplatform.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.ui.components.buttons.CustomOutlinedButton
import hse.diploma.cybersecplatform.ui.components.buttons.CustomStepper
import hse.diploma.cybersecplatform.ui.components.buttons.FilledButton
import hse.diploma.cybersecplatform.ui.components.buttons.SkipButton
import hse.diploma.cybersecplatform.ui.theme.Montserrat

@Composable
fun OnBoardingScreen(
    onNavigateToAuthorization: () -> Unit,
    onNavigateToNextPage: () -> Unit,
    viewModel: OnBoardingScreenViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val currentStep = viewModel.currentPage

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomStepper(
                    currentStep = currentStep,
                    totalSteps = 3,
                    modifier = Modifier.weight(1f)
                )
                SkipButton(onClick = onNavigateToAuthorization)
            }
            Text(
                text = currentStepToText(currentStep).first,
                fontFamily = Montserrat,
                fontWeight = FontWeight.SemiBold,
                fontSize = 28.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = currentStepToText(currentStep).second,
                fontFamily = Montserrat,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = colorResource(R.color.supporting_text),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Image(
                painter = currentStepToImage(currentStep),
                contentDescription = "Onboarding image",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            FilledButton(
                text = currentStepToButtonText(currentStep),
                onClick = {
                    if (isLastStep(currentStep)) {
                        onNavigateToAuthorization()
                    } else {
                        onNavigateToNextPage()
                    }
                }
            )
            if (isLastStep(currentStep)) {
                CustomOutlinedButton(
                    text = stringResource(R.string.auth_button),
                    onClick = onNavigateToAuthorization
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
    val mockViewModel = object : OnBoardingScreenViewModel() {
        override val currentPage: Int
            get() = 2
    }

    OnBoardingScreen(
        onNavigateToAuthorization = {},
        onNavigateToNextPage = {},
        viewModel = mockViewModel
    )
}
