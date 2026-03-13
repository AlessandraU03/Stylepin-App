package com.ale.stylepin.core.hardware.data

import android.content.Context
import androidx.biometric.BiometricManager as AndroidBiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.ale.stylepin.core.hardware.domain.BiometricManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidBiometricManager @Inject constructor(
    @ApplicationContext private val context: Context
) : BiometricManager {

    private val biometricManager = AndroidBiometricManager.from(context)

    override fun canAuthenticate(): Boolean {
        return biometricManager.canAuthenticate(
            AndroidBiometricManager.Authenticators.BIOMETRIC_STRONG or 
            AndroidBiometricManager.Authenticators.DEVICE_CREDENTIAL
        ) == AndroidBiometricManager.BIOMETRIC_SUCCESS
    }

    override fun authenticate(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        onError: (Int, CharSequence) -> Unit,
        onFailed: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errorCode, errString)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess(result)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailed()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(
                AndroidBiometricManager.Authenticators.BIOMETRIC_STRONG or 
                AndroidBiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
