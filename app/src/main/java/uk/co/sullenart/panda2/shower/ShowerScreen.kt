package uk.co.sullenart.panda2.shower

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import org.koin.androidx.compose.getViewModel
import uk.co.sullenart.panda2.R
import uk.co.sullenart.panda2.ShowError
import uk.co.sullenart.panda2.ShowLoading
import uk.co.sullenart.panda2.ShowTitle
import uk.co.sullenart.panda2.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowerScreen(
    viewModel: ShowerViewModel = getViewModel()
) {
    Box(
        Modifier.fillMaxSize()
    ) {
        val state = viewModel.uiState

        if (state is UiState.Loading) {
            ShowLoading()
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin))
        ) {
            ShowTitle("Shower")

            Text(
                buildAnnotatedString {
                    append("Current humidity: ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(viewModel.humidity?.let { "$it%" } ?: "waiting...")
                    }
                }
            )

            Text(
                buildAnnotatedString {
                    append("Status: ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(viewModel.status ?: "waiting...")
                    }
                }
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin)),
            ) {
                TextField(
                    modifier = Modifier
                        .weight(1f),
                    value = viewModel.onLevel,
                    onValueChange = { viewModel.onLevelChange(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Extractor on") },
                )
                TextField(
                    modifier = Modifier
                        .weight(1f),
                    value = viewModel.offLevel,
                    onValueChange = { viewModel.offLevelChange(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Extractor off") },
                )
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Button(
                    onClick = viewModel::sendLevels
                ) {
                    Text("Set levels")
                }
            }

            if (state is UiState.Error) {
                ShowError(state.message)
            }
        }
    }
}
