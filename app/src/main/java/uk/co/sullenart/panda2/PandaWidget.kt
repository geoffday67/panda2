package uk.co.sullenart.panda2

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.background
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth

class PandaWidget : GlanceAppWidget() {
    @Composable
    override fun Content() {
        Row(
            modifier = GlanceModifier.fillMaxWidth().background(Color.Blue),
        ) {
            /*Image(
                modifier = GlanceModifier
                    .fillMaxHeight(),
                provider = ImageProvider(R.drawable.ic_power_on),
                contentDescription = null,
            )*/
        }
    }
}