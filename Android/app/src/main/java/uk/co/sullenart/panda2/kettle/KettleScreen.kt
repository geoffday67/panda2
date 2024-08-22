package uk.co.sullenart.panda2.kettle

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
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
fun KettleScreen(
    viewModel: KettleViewModel = getViewModel()
) {
    Box(
        Modifier.fillMaxSize()
    ) {
        val state = viewModel.uiState

        if (state is UiState.Loading) {
            ShowLoading()
        }

        Column {
            ShowTitle("Kettle")

            Row(
                Modifier
                    .padding(top = dimensionResource(R.dimen.margin))
                    .fillMaxWidth()
                    .fillMaxHeight(0.2f),
                horizontalArrangement = Arrangement.Center,
            ) {
                Image(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false),
                        ) { viewModel.kettleOn() }
                        .fillMaxHeight()
                        .aspectRatio(1.0f),
                    painter = painterResource(R.drawable.ic_kettle),
                    colorFilter = ColorFilter.tint(Color.Green),
                    contentDescription = null,
                )
            }

            if (state is UiState.Error) {
                ShowError(state.message)
            }
        }
    }
}
