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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
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
        var power by remember { mutableStateOf("") }
        val indicatorAlpha = remember { Animatable(0f) }

        LaunchedEffect(Unit) {
            viewModel.power.collect {
                power = it
                indicatorAlpha.animateTo(1f)
                indicatorAlpha.animateTo(0f)
            }
        }

        if (viewModel.uiState is UiState.Loading) {
            ShowLoading()
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin))
        ) {
            ShowTitle("Shower")

            Power(
                power = power,
                indicatorAlpha = indicatorAlpha.value
            )

            Status(
                status = viewModel.status,
            )

            RunOn(
                value = viewModel.runOnSecondsString,
                onChange = viewModel::setRunOn,
                onSet = viewModel::sendRunOn,
            )
        }

        (viewModel.uiState as? UiState.Error)?.let {
            ShowError(it.message)
        }
    }
}

@Composable
private fun Power(
    power: String,
    indicatorAlpha: Float,
) {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            buildAnnotatedString {
                append("Current power: ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(power)
                }
            }
        )
        Spacer(Modifier.width(dimensionResource(R.dimen.small_margin)))
        Box(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.inversePrimary.copy(alpha = indicatorAlpha), shape = CircleShape)
                .fillMaxHeight(0.5f)
                .aspectRatio(1f)
        ) {}
    }
}

@Composable
private fun Status(
    status: String?,
) {
    Text(
        buildAnnotatedString {
            append("Status: ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(status ?: "waiting...")
            }
        }
    )
}

@Composable
private fun RunOn(
    value: String?,
    onChange: (String) -> Unit,
    onSet: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth(0.8f),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = value ?: "",
            onValueChange = { onChange(it) },
            label = { Text("Run on seconds") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = value != null,
        )
        Button(
            modifier = Modifier.padding(start = dimensionResource(R.dimen.margin)),
            onClick = onSet,
            enabled = value?.isNotBlank() ?: false,
        ) {
            Text("Set")
        }
    }
}

@Composable
private fun ShowerContent(
    fanOn: () -> Unit,
    fanOff: () -> Unit,
    immediateEnabled: Boolean,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin))
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Button(
                onClick = fanOn,
                enabled = immediateEnabled,
            ) {
                Text(
                    text = stringResource(R.string.fan_on),
                )
            }

            Button(
                onClick = fanOff,
                enabled = immediateEnabled,
            ) {
                Text(
                    text = stringResource(R.string.fan_off),
                )
            }
        }

    }
}

@Preview
@Composable
fun ContentPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin))
    ) {
        ShowTitle("Shower Preview")

        Power(
            power = "off",
            indicatorAlpha = 1f,
        )

        Status(
            status = "Preview",
        )

        RunOn(
            value = "1234",
            onChange = {},
            onSet = {},
        )
    }
}
