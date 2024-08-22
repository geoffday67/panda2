package uk.co.sullenart.panda2.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.getViewModel
import uk.co.sullenart.panda2.ObserveLifecycleEvents
import uk.co.sullenart.panda2.R
import uk.co.sullenart.panda2.ShowError
import uk.co.sullenart.panda2.ShowLoading
import uk.co.sullenart.panda2.ShowTitle
import uk.co.sullenart.panda2.UiState

@Composable
fun TimerScreen(
    viewModel: TimerViewModel = getViewModel()
) {
    viewModel.ObserveLifecycleEvents(LocalLifecycleOwner.current.lifecycle)
    val remaining = viewModel.remaining.collectAsStateWithLifecycle(initialValue = null)

    Box(
        Modifier.fillMaxSize()
    ) {
        //var remaining: Int? by remember { mutableStateOf(null) }

        /*LaunchedEffect(Unit) {
            viewModel.remaining.collect {
                remaining = it
            }
        }*/

        if (viewModel.uiState is UiState.Loading) {
            ShowLoading()
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin))
        ) {
            ShowTitle("Timer")

            Text(remaining.toString())

            (viewModel.uiState as? UiState.Error)?.let {
                ShowError(it.message)
            }
        }
    }
}