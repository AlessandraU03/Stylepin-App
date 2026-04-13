package com.ale.stylepin.features.notifications.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.notifications.domain.entities.Notification
import com.ale.stylepin.features.notifications.domain.usecases.GetNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsUiState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        Log.d("NOTIF_VM", "ViewModel creado, llamando getNotifications()")
        getNotifications()
    }

    fun getNotifications() {
        Log.d("NOTIF_VM", "getNotifications() iniciado")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            Log.d("NOTIF_VM", "Llamando al use case...")

            try {
                getNotificationsUseCase()
                    .onSuccess { notifications ->
                        Log.d("NOTIF_VM", "✅ Éxito: ${notifications.size} notificaciones")
                        _uiState.value = _uiState.value.copy(
                            notifications = notifications,
                            isLoading = false
                        )
                    }
                    .onFailure { error ->
                        Log.e("NOTIF_VM", "❌ Error: ${error.message}", error)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
            } catch (e: Exception) {
                Log.e("NOTIF_VM", "❌ Excepción: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}