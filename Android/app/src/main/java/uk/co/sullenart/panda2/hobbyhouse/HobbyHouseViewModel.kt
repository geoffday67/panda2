package uk.co.sullenart.panda2.hobbyhouse

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.co.sullenart.panda2.MqttManager
import uk.co.sullenart.panda2.UiState
import java.security.Security
import kotlin.math.roundToInt

class HobbyHouseViewModel(
    private val mqttManager: MqttManager,
) : ViewModel() {
    var uiState: UiState by mutableStateOf(UiState.Idle)

    val colours = arrayOf(
        Color(0xD4, 0x00, 0x2A),
        Color(0xFF, 0x00, 0x00),
        Color(0xD4, 0x2A, 0x00),
        Color(0xAA, 0x55, 0x00),
        Color(0x80, 0x80, 0x00),
        Color(0x55, 0xAA, 0x00),
        Color(0x2A, 0xD4, 0x00),
        Color(0x00, 0xFF, 0x00),
        Color(0x00, 0xD4, 0x2A),
        Color(0x00, 0xAA, 0x55),
        Color(0x00, 0x80, 0x80),
        Color(0x00, 0x55, 0xAA),
        Color(0x00, 0x2A, 0xD4),
        Color(0x00, 0x00, 0xFF),
        Color(0x2A, 0x00, 0xD4),
        Color(0x55, 0x00, 0xAA),
        Color(0x80, 0x00, 0x80),
        Color(0xAA, 0x00, 0x55),
    )

    private sealed interface Command {
        data class Lights(
            val colour: Colour? = null,
            val brightness: Int? = null,
            val active: Boolean,
            val enabled: Boolean,
        ) : Command {
            data class Colour(
                val red: Int,
                val green: Int,
                val blue: Int,
            )
        }

        data class Security(
            val state: String,
        ) : Command
    }

    private fun sendCommand(command: Command, topic: String) {
        viewModelScope.launch {
            val text = Gson().toJson(command)
            uiState = UiState.Loading
            try {
                mqttManager.connect()
                mqttManager.publish(topic, text)
                mqttManager.disconnect()
                uiState = UiState.Idle
            } catch (e: Exception) {
                setError(e.message.toString())
            }
        }
    }

    fun chooseColour(colour: Color) {
        val command = Command.Lights(
            brightness = 100,
            enabled = true,
            active = true,
            colour = Command.Lights.Colour(
                red = (colour.red * 255f).roundToInt(),
                green = (colour.green * 255f).roundToInt(),
                blue = (colour.blue * 255f).roundToInt(),
            ),
        )
        sendCommand(command, MQTT_LIGHTS_TOPIC)
    }

    fun lightsOff() {
        val command = Command.Lights(
            enabled = false,
            active = false,
        )
        sendCommand(command, MQTT_LIGHTS_TOPIC)
    }

    fun securityOff() {
        val command = Command.Security(
            state = "off",
        )
        sendCommand(command, MQTT_SECURITY_TOPIC)
    }

    fun securityOn() {
        val command = Command.Security(
            state = "on",
        )
        sendCommand(command, MQTT_SECURITY_TOPIC)
    }

    private suspend fun setError(message: String) {
        uiState = UiState.Error(message)
        delay(2000)
        uiState = UiState.Idle
    }

    companion object {
        const val MQTT_LIGHTS_TOPIC = "hobby/lights"
        const val MQTT_SECURITY_TOPIC = "hobby/security"
    }
}
