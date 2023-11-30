package uk.co.sullenart.panda2.xmaslights

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.getViewModel
import uk.co.sullenart.panda2.R
import uk.co.sullenart.panda2.ShowError
import uk.co.sullenart.panda2.ShowLoading
import uk.co.sullenart.panda2.ShowTitle
import uk.co.sullenart.panda2.UiState

@Composable
fun XmasLightsScreen(
    viewModel: XmasLightsViewModel = getViewModel()
) {
    XmasLightsContent(
        lightsOn = viewModel::lightsOn,
        lightsOff = viewModel::lightsOff,
        state = viewModel.uiState,
    )
}

@Composable
private fun XmasLightsContent(
    lightsOn: () -> Unit,
    lightsOff: () -> Unit,
    state: UiState,
) {
    Box(
        Modifier.fillMaxSize()
    ) {
        if (state is UiState.Loading) {
            ShowLoading()
        }

        Column {
            ShowTitle("Xmas Lights")

            Row(
                Modifier
                    .padding(top = dimensionResource(R.dimen.small_margin))
                    .fillMaxWidth()
                    .fillMaxHeight(0.2f),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Image(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false),
                        ) { lightsOn() }
                        .fillMaxHeight()
                        .aspectRatio(1.0f),
                    painter = painterResource(R.drawable.ic_power_on),
                    colorFilter = ColorFilter.tint(Color.Green),
                    contentDescription = null,
                )
                Image(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false),
                        ) { lightsOff() }
                        .fillMaxHeight()
                        .aspectRatio(1.0f),
                    painter = painterResource(R.drawable.ic_power_off),
                    colorFilter = ColorFilter.tint(Color.Red),
                    contentDescription = null,
                )
            }

            if (state is UiState.Error) {
                ShowError(state.message)
            }
        }
    }
}

@Preview
@Composable
fun PreviewIdle() {
    XmasLightsContent(
        lightsOn = {},
        lightsOff = {},
        state = UiState.Idle,
    )
}