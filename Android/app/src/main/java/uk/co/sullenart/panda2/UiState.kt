package uk.co.sullenart.panda2

interface UiState {
    object Idle : UiState
    object Loading : UiState
    class Error(val message: String) : UiState
}
