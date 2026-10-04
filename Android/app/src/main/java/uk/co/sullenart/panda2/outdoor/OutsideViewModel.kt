package uk.co.sullenart.panda2.outside

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import uk.co.sullenart.panda2.MqttManager
import uk.co.sullenart.panda2.UiState

class OutsideViewModel(
    private val mqttManager: MqttManager,
) : ViewModel(), DefaultLifecycleObserver {
    var uiState: UiState by mutableStateOf(UiState.Idle)

    private val _temperature = MutableSharedFlow<String>()
    val temperature: Flow<String>
        get() = _temperature.asSharedFlow()

    private val _humidity = MutableSharedFlow<String>()
    val humidity: Flow<String>
        get() = _humidity.asSharedFlow()

    private val _battery = MutableSharedFlow<Float>()
    val battery: Flow<Float>
        get() = _battery.asSharedFlow()

    override fun onResume(owner: LifecycleOwner) {
        viewModelScope.launch {
            _temperature.emit("waiting...")
            _humidity.emit("waiting...")
            mqttManager.connect()

            launch {
                mqttManager.subscribe(TEMPERATURE_TOPIC).collect {
                    _temperature.emit("$it°C")
                }
            }

            launch {
                mqttManager.subscribe(HUMIDITY_TOPIC).collect {
                    _humidity.emit("Humidity $it%")
                }
            }

            launch {
                mqttManager.subscribe(BATTERY_TOPIC).collect {
                    try {
                        _battery.emit(it.toFloat())
                    } catch (_: Exception) {
                    }
                }
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        viewModelScope.launch {
            mqttManager.disconnect()
        }
    }

    companion object {
        const val TEMPERATURE_TOPIC = "sensors/temp-1"
        const val HUMIDITY_TOPIC = "sensors/humidity-1"
        const val BATTERY_TOPIC = "sensors/battery-1"
    }
}
