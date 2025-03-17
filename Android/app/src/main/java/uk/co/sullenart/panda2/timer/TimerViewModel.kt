package uk.co.sullenart.panda2.timer

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

class TimerViewModel(
    private val mqttManager: MqttManager,
) : ViewModel(), DefaultLifecycleObserver {
    var uiState: UiState by mutableStateOf(UiState.Idle)

    private val _remaining = MutableSharedFlow<Int?>()
    val remaining: Flow<Int?>
        get() = _remaining.asSharedFlow()

    override fun onResume(owner: LifecycleOwner) {
        viewModelScope.launch {
            mqttManager.connect()

            launch {
                mqttManager.subscribe(REMAINING_TOPIC).collect {
                    _remaining.emit(it.toIntOrNull())
                }
            }
        }
    }

    companion object {
        const val REMAINING_TOPIC = "mama-timer/remaining"
    }
}