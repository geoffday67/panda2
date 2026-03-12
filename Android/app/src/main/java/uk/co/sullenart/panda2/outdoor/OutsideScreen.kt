package uk.co.sullenart.panda2.outside

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import uk.co.sullenart.panda2.ObserveLifecycleEvents
import uk.co.sullenart.panda2.R
import uk.co.sullenart.panda2.ShowError
import uk.co.sullenart.panda2.ShowLoading
import uk.co.sullenart.panda2.ShowTitle
import uk.co.sullenart.panda2.UiState

@Composable
fun OutsideScreen(
    viewModel: OutsideViewModel = getViewModel()
) {
    viewModel.ObserveLifecycleEvents(LocalLifecycleOwner.current.lifecycle)

    Box(
        Modifier.fillMaxSize()
    ) {
        var temperature by remember { mutableStateOf("") }
        val temperatureAlpha = remember { Animatable(0f) }

        var humidity by remember { mutableStateOf("") }
        val humidityAlpha = remember { Animatable(0f) }

        LaunchedEffect(Unit) {
            launch {
                viewModel.temperature.collect {
                    temperature = it
                    temperatureAlpha.animateTo(1f)
                    temperatureAlpha.animateTo(0f)
                }
            }

            launch {
                viewModel.humidity.collect {
                    humidity = it
                    humidityAlpha.animateTo(1f)
                    humidityAlpha.animateTo(0f)
                }
            }
        }

        if (viewModel.uiState is UiState.Loading) {
            ShowLoading()
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin))
        ) {
            ShowTitle("Outside")

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.thermometer),
                    contentDescription = null,
                )
            }

            Temperature(
                temperature = temperature,
                indicatorAlpha = temperatureAlpha.value
            )

            Humidity(
                humidity = humidity,
                indicatorAlpha = humidityAlpha.value
            )
        }

        (viewModel.uiState as? UiState.Error)?.let {
            ShowError(it.message)
        }
    }
}

@Composable
private fun Temperature(
    temperature: String,
    indicatorAlpha: Float,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
        ) {}
        Box(
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = temperature,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = dimensionResource(R.dimen.margin))
                .weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.inversePrimary.copy(alpha = indicatorAlpha), shape = CircleShape)
                    .height(20.dp)
                    .aspectRatio(1f)
            ) {}
        }
    }
}

@Composable
private fun Humidity(
    humidity: String,
    indicatorAlpha: Float,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
        ) {}
        Box(
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = humidity,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = dimensionResource(R.dimen.margin))
                .weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.inversePrimary.copy(alpha = indicatorAlpha), shape = CircleShape)
                    .height(20.dp)
                    .aspectRatio(1f)
            ) {}
        }
    }
}

@Preview
@Composable
fun ContentPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin))
    ) {
        ShowTitle("Outside")

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.thermometer),
                contentDescription = null,
            )
        }

        Temperature(
            temperature = "12.3°C",
            indicatorAlpha = 1f,
        )

        Humidity(
            humidity = "Humidity 45.6%",
            indicatorAlpha = 1f,
        )
    }
}
