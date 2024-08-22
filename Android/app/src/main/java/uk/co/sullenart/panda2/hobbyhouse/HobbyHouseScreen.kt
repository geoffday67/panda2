package uk.co.sullenart.panda2.hobbyhouse

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import uk.co.sullenart.panda2.CircularLayout
import uk.co.sullenart.panda2.R
import uk.co.sullenart.panda2.ShowError
import uk.co.sullenart.panda2.ShowLoading
import uk.co.sullenart.panda2.ShowTitle
import uk.co.sullenart.panda2.UiState

@Composable
fun HobbyHouseScreen(
    viewModel: HobbyHouseViewModel = getViewModel()
) {
    Box(
        Modifier.fillMaxSize()
    ) {
        val state = viewModel.uiState

        if (state is UiState.Loading) {
            ShowLoading()
        }

        Column(
            Modifier.fillMaxHeight(),
        ) {
            ShowTitle("Hobby House")

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

            Spacer(
                Modifier.weight(1f)
            )

            Column(
                Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = stringResource(R.string.security_title),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                )
                Row(
                    Modifier
                        .padding(vertical = dimensionResource(R.dimen.margin))
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    Button(
                        modifier = Modifier.widthIn(100.dp),
                        onClick = viewModel::securityOn,
                    ) {
                        Text(
                            text = stringResource(R.string.on),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                    Button(
                        modifier = Modifier.widthIn(100.dp),
                        onClick = viewModel::securityOff,
                    ) {
                        Text(
                            text = stringResource(R.string.off),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }

            if (state is UiState.Error) {
                ShowError(state.message)
            }
        }
    }
}
