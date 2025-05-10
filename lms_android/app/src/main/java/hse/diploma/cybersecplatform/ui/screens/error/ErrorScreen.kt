package hse.diploma.cybersecplatform.ui.screens.error

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hse.diploma.cybersecplatform.R
import hse.diploma.cybersecplatform.domain.model.ErrorType
import hse.diploma.cybersecplatform.ui.components.buttons.CustomOutlinedButton
import hse.diploma.cybersecplatform.ui.theme.Montserrat

@Composable
fun ErrorScreen(
    errorType: ErrorType,
    onReload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = errorImage(errorType),
            contentDescription = "Error",
            modifier = Modifier.size(120.dp),
        )
        Spacer(Modifier.height(32.dp))
        Text(
            text = errorText(errorType),
            fontFamily = Montserrat,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(32.dp))
        CustomOutlinedButton(
            text = stringResource(R.string.reload_button),
            enabled = true,
            onClick = onReload,
        )
    }
}

@Composable
private fun errorText(errorType: ErrorType): String {
    return when (errorType) {
        ErrorType.NoInternet -> stringResource(R.string.no_internet_error)
        is ErrorType.Server -> stringResource(R.string.server_error)
        ErrorType.Other -> stringResource(R.string.other_error)
    }
}

@Composable
private fun errorImage(errorType: ErrorType): Painter {
    return when (errorType) {
        ErrorType.NoInternet -> painterResource(R.drawable.ic_no_wifi)
        // TODO: show error code instead of image
        is ErrorType.Server -> painterResource(R.drawable.ic_server_error)
        ErrorType.Other -> painterResource(R.drawable.ic_other_error)
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorScreenPreview() {
    ErrorScreen(
        errorType = ErrorType.Other,
        onReload = {},
    )
}
