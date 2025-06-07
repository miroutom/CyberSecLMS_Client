package hse.diploma.cybersecplatform.ui.components.dialogs

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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.domain.model.Language
import hse.diploma.cybersecplatform.ui.components.buttons.FilledButton
import hse.diploma.cybersecplatform.ui.theme.Montserrat

@Composable
fun LanguageChooserDialog(
    currentLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.language_setting), fontFamily = Montserrat) },
        text = {
            Column {
                Language.entries.forEach { language ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(language) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = currentLanguage == language,
                            colors =
                                RadioButtonDefaults.colors(
                                    selectedColor = colorResource(R.color.button_enabled),
                                ),
                            onClick = { onLanguageSelected(language) },
                        )
                        Text(language.name, fontFamily = Montserrat)
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

@Preview(showBackground = true, apiLevel = 30)
@Composable
private fun LanguageChooserDialogPreview() {
    LanguageChooserDialog(
        currentLanguage = Language.ENGLISH,
        onLanguageSelected = {},
        onDismiss = {},
    )
}
