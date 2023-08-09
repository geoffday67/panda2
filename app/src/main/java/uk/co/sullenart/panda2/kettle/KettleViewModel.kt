package uk.co.sullenart.panda2.kettle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.co.sullenart.panda2.MqttManager
import uk.co.sullenart.panda2.UiState
import uk.co.sullenart.panda2.kitchen.KitchenViewModel
import kotlin.math.roundToInt

class KettleViewModel(
    private val mqttManager: MqttManager,
) : ViewModel() {
    var uiState: UiState by mutableStateOf(UiState.Idle)

    private fun sendCommand(text: String) {
        viewModelScope.launch {
            uiState = UiState.Loading
            try {
                mqttManager.connect()
                mqttManager.publish(MQTT_TOPIC, text)
                mqttManager.disconnect()
                uiState = UiState.Idle
            } catch (e: Exception) {
                setError(e.message.toString())
            }
        }
    }

    fun kettleOn() {
        sendCommand("on")
    }

    private suspend fun setError(message: String) {
        uiState = UiState.Error(message)
        delay(2000)
        uiState = UiState.Idle
    }

    companion object {
        const val MQTT_TOPIC = "switches/kettle"
    }
}
