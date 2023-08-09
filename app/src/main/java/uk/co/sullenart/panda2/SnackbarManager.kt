package uk.co.sullenart.panda2

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class SnackbarManager {
    private val _messages = MutableSharedFlow<String>()
    val messages: Flow<String>
        get() = _messages

    suspend fun add(message: String) {
        _messages.emit(message)
    }
}