package hse.diploma.cybersecplatform.ui.components.dialogs

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.domain.model.AppTheme
import hse.diploma.cybersecplatform.ui.components.buttons.FilledButton
import hse.diploma.cybersecplatform.ui.theme.Montserrat

@Composable
fun ThemeChooserDialog(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.theme_setting), fontFamily = Montserrat) },
        text = {
            Column {
                AppTheme.entries.forEach { theme ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                Toast.makeText(context, "Selected theme: ${theme.name}", Toast.LENGTH_SHORT).show()
                                onThemeSelected(theme)
                            }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = currentTheme == theme,
                            colors =
                                RadioButtonDefaults.colors(
                                    selectedColor = colorResource(R.color.button_enabled),
                                ),
                            onClick = {
                                Toast.makeText(context, "Selected theme: ${theme.name}", Toast.LENGTH_SHORT).show()
                                onThemeSelected(theme)
                            },
                        )
                        Text(theme.name, fontFamily = Montserrat)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            FilledButton(
                text = stringResource(R.string.cancel_button),
                onClick = onDismiss,
            )
        },
        containerColor = colorResource(R.color.dialog_color),
        tonalElevation = 8.dp,
    )
}

@PreviewLightDark
@Composable
private fun ThemeChooserDialogPreview() {
    ThemeChooserDialog(
        currentTheme = AppTheme.LIGHT,
        onThemeSelected = {},
        onDismiss = {},
    )
}
