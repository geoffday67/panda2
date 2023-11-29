package uk.co.sullenart.panda2.shower

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import org.koin.androidx.compose.getViewModel
import uk.co.sullenart.panda2.ObserveLifecycleEvents
import uk.co.sullenart.panda2.R
import uk.co.sullenart.panda2.ShowError
import uk.co.sullenart.panda2.ShowLoading
import uk.co.sullenart.panda2.ShowTitle
import uk.co.sullenart.panda2.UiState

@Composable
fun ShowerScreen(
    viewModel: ShowerViewModel = getViewModel()
) {
    viewModel.ObserveLifecycleEvents(LocalLifecycleOwner.current.lifecycle)

    Box(
        Modifier.fillMaxSize()
    ) {
        val state = viewModel.uiState
        var humidity by remember { mutableStateOf("waiting...") }
        val indicatorAlpha = remember { Animatable(0f) }

        LaunchedEffect(Unit) {
            viewModel.humidity.collect {
                humidity = "$it%"
                indicatorAlpha.animateTo(1f)
                indicatorAlpha.animateTo(0f)
            }
        }

        if (state is UiState.Loading) {
            ShowLoading()
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin))
        ) {
            ShowTitle("Shower")

            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    buildAnnotatedString {
                        append("Current humidity: ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(humidity)
                        }
                    }
                )
                Spacer(Modifier.width(dimensionResource(R.dimen.small_margin)))
                Box(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.inversePrimary.copy(alpha = indicatorAlpha.value), shape = CircleShape)
                        .fillMaxHeight(0.5f)
                        .aspectRatio(1f)
                ) {}
            }

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
