package uk.co.sullenart.panda2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun ShowTitle(
    title: String,
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.secondary),
        )
    }
}

@Composable
fun ShowLoading() {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ShowError(
    message: String,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(R.dimen.margin)),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = message,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun CircularLayout(
    modifier: Modifier = Modifier,
    radiusRatio: Float = 0.35f,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val newConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { measurable ->
            measurable.measure(newConstraints)
        }

        val centreX = constraints.maxWidth / 2
        val centreY = constraints.maxHeight / 2
        var angle = -PI / 2
        val angleIncrement = 2 * PI / placeables.size
        val radius = min(constraints.maxWidth, constraints.maxHeight) * radiusRatio

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEach {
                val x = radius * cos(angle)
                val y = radius * sin(angle)
                it.place(
                    (centreX + x - it.width / 2).roundToInt(),
                    (centreY + y - it.height / 2).roundToInt()
                )
                angle += angleIncrement
            }
        }
    }
}
