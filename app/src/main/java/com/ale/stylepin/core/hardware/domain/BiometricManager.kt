package com.ale.stylepin.core.hardware.domain

import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity

interface BiometricManager {
    fun canAuthenticate(): Boolean
    fun authenticate(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        onError: (Int, CharSequence) -> Unit,
        onFailed: () -> Unit
    )
}
