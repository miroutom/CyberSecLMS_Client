package hse.diploma.cybersecplatform.ui.screens.courses

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.domain.model.TabType
import hse.diploma.cybersecplatform.ui.components.buttons.TabButton

@Composable
fun CoursesTabs(
    selectedTab: TabType,
    onTabSelected: (TabType) -> Unit,
) {
    Row {
        TabButton(
            textId = R.string.started_courses_tab_button,
            selected = selectedTab == TabType.STARTED,
            onClick = { onTabSelected(TabType.STARTED) },
        )
        TabButton(
            textId = R.string.completed_courses_tab_button,
            selected = selectedTab == TabType.COMPLETED,
            onClick = { onTabSelected(TabType.COMPLETED) },
        )
    }
}
