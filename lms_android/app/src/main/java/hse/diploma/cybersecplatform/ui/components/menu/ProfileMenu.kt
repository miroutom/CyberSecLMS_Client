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

@Composable
fun ProfileMenu(
    modifier: Modifier = Modifier,
    onTheoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSupportClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    val dividerColor = colorResource(R.color.menu_divider)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        ProfileMenuItem(
            icon = painterResource(R.drawable.ic_theory),
            text = stringResource(R.string.profile_theory_section),
            onClick = onTheoryClick,
        )
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 8.dp),
            color = dividerColor,
        )
        ProfileMenuItem(
            icon = painterResource(R.drawable.ic_settings),
            text = stringResource(R.string.profile_settings_section),
            onClick = onSettingsClick,
        )
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 8.dp),
            color = dividerColor,
        )
        ProfileMenuItem(
            icon = painterResource(R.drawable.ic_support),
            text = stringResource(R.string.profile_support_section),
            onClick = onSupportClick,
        )
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 8.dp),
            color = dividerColor,
        )
        ProfileMenuItem(
            icon = painterResource(R.drawable.ic_logout),
            text = stringResource(R.string.profile_logout),
            iconTint = colorResource(R.color.logout),
            textColor = colorResource(R.color.logout),
            onClick = onLogoutClick,
        )
    }
}
