package com.ale.stylepin.features.profile.presentation.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: SharedPreferences
) : ViewModel() {

    fun logout() {
        prefs.edit().remove("auth_token").apply()
    }
}