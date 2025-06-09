package hse.diploma.cybersecplatform.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.domain.model.Course
import hse.diploma.cybersecplatform.ui.components.buttons.RestartCourseButton
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.utils.toVulnerabilityType

@Composable
fun CompletedCourseCard(
    course: Course,
    onCardClick: () -> Unit,
    onRestartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val config = course.vulnerabilityType.toVulnerabilityType().config

    ElevatedCard(
        onClick = onCardClick,
        modifier =
            modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        shape = RoundedCornerShape(32.dp),
        colors =
            CardDefaults.elevatedCardColors(
                containerColor = colorResource(config.backgroundColor),
            ),
    ) {
        Column(
            modifier = Modifier.wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(122.dp)
                        .background(
                            brush = config.carGradient,
                            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                        ),
            ) {
                Text(
                    text = stringResource(config.titleTextId),
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    style = TextStyle.Default.copy(brush = config.titleGradient),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(config.subtitleTextId),
                fontFamily = Montserrat,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = colorResource(R.color.main_text_color),
                textAlign = TextAlign.Center,
            )
            Box(
                modifier =
                    modifier.padding(
                        top = 4.dp,
                        end = 16.dp,
                        bottom = 16.dp,
                        start = 16.dp,
                    ),
            ) {
                RestartCourseButton(
                    color = colorResource(config.signatureColor),
                    onClick = onRestartClick,
                    modifier = Modifier.testTag("RestartCourseButton"),
                )
            }
        }
    }
}

@Preview(showBackground = true, apiLevel = 30)
@Composable
private fun CompletedCourseCardPreview() {
    CyberSecPlatformTheme {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CompletedCourseCard(
                course =
                    Course(
                        id = 1,
                        title = "XSS Attacks Course",
                        description = "Learn about different types of XSS vulnerabilities",
                        vulnerabilityType = "XSS",
                        difficultyLevel = "medium",
                        category = "web",
                        tasks = emptyList(),
                        completedTasks = 3,
                        tasksCount = 10,
                    ),
                onCardClick = {},
                onRestartClick = {},
            )
        }
    }
}
