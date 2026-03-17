package com.ale.stylepin.features.community.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.core.network.StylePinWebSocketManager
import com.ale.stylepin.features.community.domain.entities.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val webSocketManager: StylePinWebSocketManager
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications = _notifications.asStateFlow()

    init {
        // Escuchamos el WS y agregamos las nuevas alertas hasta arriba de la lista
        viewModelScope.launch {
            webSocketManager.notifications.collect { newNotif ->
                _notifications.update { currentList ->
                    listOf(newNotif) + currentList
                }
            }
        }
    }
}