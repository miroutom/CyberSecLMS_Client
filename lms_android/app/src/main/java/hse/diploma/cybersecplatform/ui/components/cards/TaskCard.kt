package hse.diploma.cybersecplatform.ui.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import hse.diploma.cybersecplatform.domain.model.Task
import hse.diploma.cybersecplatform.ui.model.Difficulty
import hse.diploma.cybersecplatform.ui.model.VulnerabilityType
import hse.diploma.cybersecplatform.ui.theme.CyberSecPlatformTheme
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.utils.toDifficulty
import hse.diploma.cybersecplatform.utils.toVulnerabilityType

@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val vulnerabilityConfig = task.vulnerabilityType.toVulnerabilityType().config
    val difficultyValue = stringResource(task.difficulty.toDifficulty().value)

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

@Composable
@Preview(showBackground = true, apiLevel = 30)
fun TaskCardPreview() {
    CyberSecPlatformTheme {
        Column {
            TaskCard(
                task =
                    Task(
                        id = 1,
                        courseId = 101,
                        title = "XSS в HTML-контексте",
                        description = "Сохранение XSS в HTML-контексте без кодирования",
                        content = "Детальное описание задания...",
                        solution = "Решение задания...",
                        vulnerabilityType = VulnerabilityType.XSS.name,
                        number = 1,
                        difficulty = Difficulty.EASY.name,
                        type = "XSS",
                        points = 10,
                        isCompleted = false,
                    ),
                onClick = {},
            )

            TaskCard(
                task =
                    Task(
                        id = 2,
                        courseId = 102,
                        title = "CSRF с проверкой токена",
                        description = "CSRF, где проверка токена зависит от наличия токена",
                        content = "Детальное описание задания...",
                        solution = "Решение задания...",
                        vulnerabilityType = VulnerabilityType.CSRF.name,
                        number = 10,
                        difficulty = Difficulty.HARD.name,
                        type = "CSRF",
                        points = 30,
                        isCompleted = true,
                    ),
                onClick = {},
            )
            TaskCard(
                task =
                    Task(
                        id = 3,
                        courseId = 103,
                        title = "SQL Injection UNION атака",
                        description = "Использование UNION для извлечения данных",
                        content = "Детальное описание задания...",
                        solution = "Решение задания...",
                        vulnerabilityType = VulnerabilityType.SQL.name,
                        number = 5,
                        difficulty = Difficulty.MEDIUM.name,
                        type = "SQL_INJECTION",
                        points = 20,
                        isCompleted = false,
                    ),
                onClick = {},
            )
        }
    }
}
