package hse.diploma.cybersecplatform.ui.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.domain.model.Difficulty
import hse.diploma.cybersecplatform.domain.model.Task
import hse.diploma.cybersecplatform.domain.model.VulnerabilityType
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.ui.theme.Montserrat

@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val vulnerabilityConfig = task.vulnerabilityType.config
    val difficultyValue = stringResource(task.difficulty.value)

    ElevatedCard(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        shape = RoundedCornerShape(20.dp),
        colors =
            CardDefaults.elevatedCardColors(
                containerColor = colorResource(vulnerabilityConfig.backgroundColor),
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(vulnerabilityConfig.imageId),
                contentDescription = null,
                modifier = Modifier.size(120.dp),
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(R.string.difficulty_number_of_task, difficultyValue, task.number),
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
                Text(
                    text = task.description,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                )
            }
        }
    }
}

@Preview()
@Composable
fun TaskCardPreview() {
    CyberSecPlatformTheme {
        Column {
            TaskCard(
                task =
                    Task(
                        vulnerabilityType = VulnerabilityType.XSS,
                        number = 1,
                        description = "Сохранение XSS в HTML-контексте без кодирования",
                        difficulty = Difficulty.EASY,
                    ),
                onClick = {},
            )
            Spacer(modifier = Modifier.height(16.dp))
            TaskCard(
                task =
                    Task(
                        vulnerabilityType = VulnerabilityType.CSRF,
                        number = 10,
                        description = "CSRF, где проверка токена зависит от наличия токена",
                        difficulty = Difficulty.HARD,
                    ),
                onClick = {},
            )
        }
    }
}
