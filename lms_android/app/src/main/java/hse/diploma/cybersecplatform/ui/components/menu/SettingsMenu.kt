package hse.diploma.cybersecplatform.ui.components.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import hse.diploma.cybersecplatform.R

enum class SettingsDialog {
    NONE,
    THEME,
    LANGUAGE,
    PASSWORD,
    DELETE_ACCOUNT,
}

@Composable
fun SettingsMenu(
    onThemeClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onPasswordChangeClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dividerColor = colorResource(R.color.menu_divider)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        MenuItem(
            icon = painterResource(R.drawable.ic_theme),
            text = stringResource(R.string.theme_setting),
            onClick = onThemeClick,
        )
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 8.dp),
            color = dividerColor,
        )
        MenuItem(
            icon = painterResource(R.drawable.ic_language),
            text = stringResource(R.string.language_setting),
            onClick = onLanguageClick,
        )
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 8.dp),
            color = dividerColor,
        )
        MenuItem(
            icon = painterResource(R.drawable.ic_lock),
            text = stringResource(R.string.update_password_setting),
            onClick = onPasswordChangeClick,
        )
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 8.dp),
            color = dividerColor,
        )
        MenuItem(
            icon = painterResource(R.drawable.ic_trash),
            text = stringResource(R.string.delete_account),
            iconTint = colorResource(R.color.logout),
            textColor = colorResource(R.color.logout),
            onClick = onDeleteClick,
        )
    }
}
