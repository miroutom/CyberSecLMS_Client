package hse.diploma.cybersecplatform.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.domain.model.Difficulty
import hse.diploma.cybersecplatform.ui.theme.Montserrat
import hse.diploma.cybersecplatform.ui.theme.Typography

@Composable
fun FilterSelectionDialog(
    onFilterSelected: (List<Difficulty>) -> Unit,
    onDismiss: () -> Unit,
    onClearFilters: () -> Unit,
) {
    val difficulties = Difficulty.entries.toTypedArray()

    val selectedDifficulties = remember { mutableStateListOf<Difficulty>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.filter_selection_dialog_title),
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                fontFamily = Montserrat,
            )
        },
        text = {
            Column {
                difficulties.forEach { difficulty ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp),
                    ) {
                        Checkbox(
                            checked = selectedDifficulties.contains(difficulty),
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    selectedDifficulties.add(difficulty)
                                } else {
                                    selectedDifficulties.remove(difficulty)
                                }
                            },
                            colors =
                                CheckboxDefaults.colors(
                                    checkedColor = colorResource(R.color.button_enabled),
                                    uncheckedColor = colorResource(R.color.button_enabled),
                                    checkmarkColor = Color.White,
                                ),
                        )
                        Text(
                            text = stringResource(difficulty.value),
                            style = Typography.bodyMedium,
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onFilterSelected(selectedDifficulties.toList())
                    onDismiss()
                },
                colors =
                    ButtonDefaults.filledTonalButtonColors(
                        containerColor = colorResource(R.color.button_enabled),
                        contentColor = Color.White,
                    ),
            ) {
                Text(
                    text = stringResource(R.string.confirm_button),
                    fontStyle = FontStyle.Italic,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Normal,
                )
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    selectedDifficulties.clear()
                    onClearFilters()
                },
                colors =
                    ButtonDefaults.filledTonalButtonColors(
                        containerColor = colorResource(R.color.button_enabled),
                        contentColor = Color.White,
                    ),
            ) {
                Text(
                    text = stringResource(R.string.reset_button),
                    fontStyle = FontStyle.Italic,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Normal,
                )
            }
        },
        containerColor = colorResource(R.color.dialog_color),
        tonalElevation = 8.dp,
    )
}

@Preview
@Composable
private fun FilterSelectionDialogPreview() {
    FilterSelectionDialog(
        onFilterSelected = {},
        onDismiss = {},
        onClearFilters = {},
    )
}
