package com.ale.stylepin.features.profile.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ale.stylepin.features.profile.domain.usecases.GetMyProfileUseCase
import com.ale.stylepin.features.profile.domain.usecases.UpdateProfileUseCase
import com.ale.stylepin.features.profile.domain.usecases.UploadAvatarUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileUiState(
    val email: String = "", val fullName: String = "", val bio: String = "",
    val gender: String = "other", val avatarUrl: String = "", val newAvatarUri: String? = null,
    val isLoading: Boolean = false, val isSuccess: Boolean = false, val error: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState = _uiState.asStateFlow()

    init { loadCurrentData() }

    private fun loadCurrentData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val profile = getMyProfileUseCase.execute()
                _uiState.update {
                    it.copy(isLoading = false, email = profile.email, fullName = profile.fullName,
                        bio = profile.bio, gender = profile.gender, avatarUrl = profile.avatarUrl)
                }
            } catch (e: Exception) { _uiState.update { it.copy(isLoading = false, error = "Error") } }
        }
    }

    fun onFullNameChange(value: String) = _uiState.update { it.copy(fullName = value, error = null) }
    fun onBioChange(value: String) = _uiState.update { it.copy(bio = value, error = null) }
    fun onGenderChange(value: String) = _uiState.update { it.copy(gender = value, error = null) }
    fun onAvatarSelected(uri: String) = _uiState.update { it.copy(newAvatarUri = uri, error = null) }

    fun saveProfile() {
        val currentState = _uiState.value
        if (currentState.fullName.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            var finalAvatarUrl = currentState.avatarUrl
            if (currentState.newAvatarUri != null) {
                val uploadResult = uploadAvatarUseCase.execute(currentState.newAvatarUri)
                if (uploadResult.isSuccess) { finalAvatarUrl = uploadResult.getOrNull() ?: currentState.avatarUrl }
                else { _uiState.update { it.copy(isLoading = false, error = "Error foto") }; return@launch }
            }
            val result = updateProfileUseCase.execute(currentState.fullName, currentState.bio, currentState.gender, finalAvatarUrl)
            if (result.isSuccess) _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            else _uiState.update { it.copy(isLoading = false, error = "Error perfil") }
        }
    }
}