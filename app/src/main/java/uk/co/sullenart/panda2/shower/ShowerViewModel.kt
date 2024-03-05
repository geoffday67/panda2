package uk.co.sullenart.panda2.shower

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import uk.co.sullenart.panda2.MqttManager
import uk.co.sullenart.panda2.SnackbarManager
import uk.co.sullenart.panda2.UiState

class ShowerViewModel(
    private val mqttManager: MqttManager,
    private val snackbarManager: SnackbarManager,
) : ViewModel(), DefaultLifecycleObserver {
    var uiState: UiState by mutableStateOf(UiState.Idle)
    var status: String? by mutableStateOf(null)
    var immediateEnabled by mutableStateOf(false)

    private val _power = MutableSharedFlow<String>()
    val power: Flow<String>
        get() = _power.asSharedFlow()

    override fun onResume(owner: LifecycleOwner) {
        immediateEnabled = false

        viewModelScope.launch {
            mqttManager.connect()
            immediateEnabled = true

            launch {
                mqttManager.subscribe(POWER_TOPIC).collect {
                    _power.emit(it)
                }
            }

            launch {
                val json = mqttManager.subscribe(SETTINGS_TOPIC).first()
                val settings = Gson().fromJson(json, Command.Settings::class.java)
            }

            launch {
                mqttManager.subscribe(STATUS_TOPIC).collect {
                    status = it
                }
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        viewModelScope.launch {
            mqttManager.disconnect()
        }
    }

    private sealed interface Command {
        data class Settings(
            @SerializedName("run_on_time") val runOnTime: Int,
        ) : Command

        data class Immediate(
            @SerializedName("fan") val status: String,
        ) : Command
    }

    private suspend fun sendCommand(command: Command, retain: Boolean) {
        val text = Gson().toJson(command)
        uiState = UiState.Loading
        try {
            mqttManager.publish(SETTINGS_TOPIC, text, retain = retain)
            uiState = UiState.Idle
        } catch (e: Exception) {
            setError(e.message.toString())
        }
    }

    fun fanOn() {
        viewModelScope.launch {
            sendCommand(Command.Immediate("on"), retain = false)
            snackbarManager.add("Fan turned on")
        }
    }

    fun fanOff() {
        viewModelScope.launch {
            sendCommand(Command.Immediate("off"), retain = false)
            snackbarManager.add("Fan turned off")
        }
    }

    private suspend fun setError(message: String) {
        uiState = UiState.Error(message)
        delay(2000)
        uiState = UiState.Idle
    }

    companion object {
        const val SETTINGS_TOPIC = "shower/settings"
        const val POWER_TOPIC = "shower/power"
        const val STATUS_TOPIC = "shower/status"
    }
}
