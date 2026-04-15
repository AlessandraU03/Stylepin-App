package com.ale.stylepin.features.boards.presentation.viewmodels

sealed class BoardFormEvent {
    data class NameChanged(val value: String) : BoardFormEvent()
    data class DescriptionChanged(val value: String) : BoardFormEvent()
    data class IsPrivateChanged(val value: Boolean) : BoardFormEvent()
    data class IsCollaborativeChanged(val value: Boolean) : BoardFormEvent()
    data class AddPinNotesChanged(val value: String) : BoardFormEvent()
}