package com.ale.stylepin.core.di

import android.content.Context
import com.ale.stylepin.BuildConfig
import com.ale.stylepin.core.network.StylePinApi
import com.ale.stylepin.core.network.AuthInterceptor
import com.ale.stylepin.features.auth.data.repositories.AuthRepositoryImpl
import com.ale.stylepin.features.auth.domain.repositories.AuthRepository
import com.ale.stylepin.features.pins.data.repositories.PinRepositoryImpl
import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    // 1. Acceso al almacenamiento persistente
    private val sharedPreferences = context.getSharedPreferences("stylepin_prefs", Context.MODE_PRIVATE)

    // 2. Cliente de OkHttp con el interceptor de seguridad

    // 2. Cliente de OkHttp con el interceptor de seguridad
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor {
            // Esto leerá el token que guardó el AuthRepositoryImpl al hacer login
            sharedPreferences.getString("auth_token", null)
        })
        .build()

    // 3. Generación de Retrofit inyectando el cliente
    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient) // <--- Crucial para evitar el 403 Forbidden
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val stylePinRetrofit = createRetrofit(BuildConfig.BASE_URL_STYLEPIN)

    val stylePinApi: StylePinApi by lazy {
        stylePinRetrofit.create(StylePinApi::class.java)
    }

    // --- CORRECCIÓN AQUÍ ---
    val authRepository: AuthRepository by lazy {
        // Ahora le pasamos sharedPreferences para que pueda GUARDAR el token
        AuthRepositoryImpl(stylePinApi, sharedPreferences)
    }

    val pinsRepository: PinsRepository by lazy {
        PinRepositoryImpl(stylePinApi)
    }
}