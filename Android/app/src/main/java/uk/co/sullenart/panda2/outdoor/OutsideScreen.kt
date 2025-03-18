package uk.co.sullenart.panda2.outside

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.proto.LayoutProto.HorizontalAlignment
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
        val indicatorAlpha = remember { Animatable(0f) }

        LaunchedEffect(Unit) {
            viewModel.temperature.collect {
                temperature = it
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
            ShowTitle("Outside")

            Temperature(
                temperature = temperature,
                indicatorAlpha = indicatorAlpha.value
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
    Column(
        Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small_margin)),
    ) {
        Image(
            painter = painterResource(R.drawable.thermometer),
            contentDescription = null,
        )

        Box(
            Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,

            ) {
            Text(
                text = temperature,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
            )

            Row(
                Modifier.fillMaxWidth(0.5f),
                horizontalArrangement = Arrangement.End,
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
}

@Preview
@Composable
fun ContentPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin))
    ) {
        ShowTitle("Outside")

        Temperature(
            temperature = "12.3",
            indicatorAlpha = 1f,
        )
    }
}
