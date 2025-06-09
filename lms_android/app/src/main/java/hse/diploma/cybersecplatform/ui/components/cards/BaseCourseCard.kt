package hse.diploma.cybersecplatform.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.utils.toVulnerabilityType

@Composable
fun BaseCourseCard(
    course: Course,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val config = course.vulnerabilityType.toVulnerabilityType().config

    ElevatedCard(
        onClick = onClick,
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
            Text(
                text = stringResource(R.string.tasks_count, course.tasksCount),
                fontFamily = Montserrat,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = colorResource(config.signatureColor),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                color = colorResource(config.signatureColor),
                thickness = 1.dp,
                modifier =
                    Modifier
                        .width(81.dp)
                        .height(1.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
@Preview(showBackground = true, apiLevel = 30)
private fun BaseCourseCardPreview() {
    CyberSecPlatformTheme {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            BaseCourseCard(
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
                onClick = {},
            )
        }
    }
}
