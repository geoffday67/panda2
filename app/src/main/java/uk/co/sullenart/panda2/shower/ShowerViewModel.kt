package uk.co.sullenart.panda2.shower

import android.util.Log
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import timber.log.Timber
import uk.co.sullenart.panda2.MqttManager
import uk.co.sullenart.panda2.SnackbarManager
import uk.co.sullenart.panda2.UiState

class ShowerViewModel(
    private val mqttManager: MqttManager,
    private val snackbarManager: SnackbarManager,
) : ViewModel(), DefaultLifecycleObserver {
    var uiState: UiState by mutableStateOf(UiState.Idle)
    var onLevel by mutableStateOf("")
    var offLevel by mutableStateOf("")
    var status: String? by mutableStateOf(null)
    var immediateEnabled by mutableStateOf(false)

    private val _humidity = MutableSharedFlow<Int>()
    val humidity: Flow<Int>
        get() = _humidity.asSharedFlow()

    override fun onResume(owner: LifecycleOwner) {
        immediateEnabled = false

        viewModelScope.launch {
            mqttManager.connect()
            immediateEnabled = true

            launch {
                mqttManager.subscribe(HUMIDITY_TOPIC).collect {
                    it.toIntOrNull()?.let {
                        _humidity.emit(it)
                    }
                }
            }

            launch {
                val json = mqttManager.subscribe(SETTINGS_TOPIC).first()
                val settings = Gson().fromJson(json, Command.Settings::class.java)
                onLevel = settings.onLevel.toString()
                offLevel = settings.offLevel.toString()
            }

            launch {
                mqttManager.subscribe(STATUS_TOPIC).collect {
                    status = it
                }
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        Timber.d("Lifecycle pause")
        viewModelScope.launch {
            mqttManager.disconnect()
        }
    }

    private sealed interface Command {
        data class Settings(
            @SerializedName("on_level") val onLevel: Int,
            @SerializedName("off_level") val offLevel: Int,
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

    fun onLevelChange(level: String) {
        onLevel = level.filter { it.isDigit() }
    }

    fun offLevelChange(level: String) {
        offLevel = level.filter { it.isDigit() }
    }

    fun sendLevels() {
        viewModelScope.launch {
            val command = Command.Settings(
                onLevel = onLevel.toIntOrNull() ?: 0,
                offLevel = offLevel.toIntOrNull() ?: 0,
            )
            sendCommand(command, retain = true)
            snackbarManager.add("Settings saved")
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
        const val HUMIDITY_TOPIC = "shower/humidity"
        const val STATUS_TOPIC = "shower/status"
    }
}
