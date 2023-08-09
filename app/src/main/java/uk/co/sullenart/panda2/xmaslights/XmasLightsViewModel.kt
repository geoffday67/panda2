package uk.co.sullenart.panda2.xmaslights

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.co.sullenart.panda2.MqttManager
import uk.co.sullenart.panda2.UiState

class XmasLightsViewModel(
    private val mqttManager: MqttManager,
) : ViewModel() {
    var uiState: UiState by mutableStateOf(UiState.Idle)

    private suspend fun setError(message: String) {
        uiState = UiState.Error(message)
        delay(2000)
        uiState = UiState.Idle
    }

    fun lightsOn() {
        viewModelScope.launch {
            uiState = UiState.Loading
            try {
                mqttManager.connect()
                mqttManager.publish(MQTT_TOPIC, MQTT_ON)
                mqttManager.disconnect()
                uiState = UiState.Idle
            } catch (e: Exception) {
                setError(e.message.toString())
            }
        }
    }

    fun lightsOff() {
        viewModelScope.launch {
            uiState = UiState.Loading
            try {
                mqttManager.connect()
                mqttManager.publish(MQTT_TOPIC, MQTT_OFF)
                mqttManager.disconnect()
                uiState = UiState.Idle
            } catch (e: Exception) {
                setError(e.message.toString())
            }
        }
    }

    companion object {
        const val MQTT_TOPIC = "switch/relay1"
        const val MQTT_ON = "on"
        const val MQTT_OFF = "off"
    }
}
