package uk.co.sullenart.panda2.kitchen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import uk.co.sullenart.panda2.CircularLayout
import uk.co.sullenart.panda2.R
import uk.co.sullenart.panda2.ShowError
import uk.co.sullenart.panda2.ShowLoading
import uk.co.sullenart.panda2.ShowTitle
import uk.co.sullenart.panda2.UiState

@Composable
fun KitchenScreen(
    viewModel: KitchenViewModel = getViewModel()
) {
    Box(
        Modifier.fillMaxSize()
    ) {
        val state = viewModel.uiState

        if (state is UiState.Loading) {
            ShowLoading()
        }

        Column {
            ShowTitle("Kitchen")

            CircularLayout(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                radiusRatio = 0.38f,
            ) {
                viewModel.colours.forEach {
                    Box(
                        Modifier
                            .border(2.dp, Color.Black, CircleShape)
                            .fillMaxSize(0.11f)
                            .clip(CircleShape)
                            .clickable { viewModel.chooseColour(it) }
                            .background(it)
                    )
                }
            }

            Row(
                Modifier
                    .padding(top = dimensionResource(R.dimen.small_margin))
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Button(
                    onClick = viewModel::lightsOff,
                ) {
                    Text(
                        text = stringResource(R.string.lights_off),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }

            if (state is UiState.Error) {
                ShowError(state.message)
            }
        }
    }
}
