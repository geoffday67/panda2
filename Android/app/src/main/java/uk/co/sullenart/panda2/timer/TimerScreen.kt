package uk.co.sullenart.panda2.timer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.getViewModel
import uk.co.sullenart.panda2.ObserveLifecycleEvents
import uk.co.sullenart.panda2.R
import uk.co.sullenart.panda2.ShowLoading
import uk.co.sullenart.panda2.ShowTitle
import uk.co.sullenart.panda2.UiState

@Composable
fun TimerScreen(
    viewModel: TimerViewModel = getViewModel()
) {
    viewModel.ObserveLifecycleEvents(LocalLifecycleOwner.current.lifecycle)
    val remaining = viewModel.remaining.collectAsStateWithLifecycle(initialValue = null).value

    Box(
        Modifier.fillMaxSize()
    ) {
        if (viewModel.uiState is UiState.Loading) {
            ShowLoading()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ShowTitle("Timer")
            Image(
                modifier = Modifier
                    .fillMaxWidth(),
                painter = painterResource(R.drawable.mama),
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
            Remaining(remaining)
        }
    }
}

@Composable
private fun Remaining(
    remaining: Int?,
) {
    if (remaining == null) {
        Text("Waiting...", style = MaterialTheme.typography.headlineMedium)
    } else {
        if (remaining == 0) {
            Text("Time's up!", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)
        } else {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(remaining.toString())
                    }
                    append(pluralStringResource(R.plurals.remaining, remaining))
                },
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}