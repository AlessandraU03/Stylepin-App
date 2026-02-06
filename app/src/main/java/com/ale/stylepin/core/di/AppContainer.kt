package com.ale.stylepin.core.di

import android.content.Context
import com.ale.stylepin.BuildConfig
import com.ale.stylepin.core.network.StylePinApi
import com.ale.stylepin.core.network.AuthInterceptor
import com.ale.stylepin.features.auth.data.repositories.AuthRepositoryImpl
import com.ale.stylepin.features.auth.domain.repositories.AuthRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.ale.stylepin.features.pins.data.repositories.PinRepositoryImpl
import com.ale.stylepin.features.pins.domain.repository.PinsRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppContainer(context: Context) {

    // 1. Acceso al almacenamiento persistente
    private val sharedPreferences = context.getSharedPreferences("stylepin_prefs", Context.MODE_PRIVATE)

    // 2. Cliente de OkHttp con el interceptor de seguridad

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor {
            // PEGA AQUÍ UN TOKEN QUE COPIES DIRECTO DE TU SWAGGER
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJmMmM4ZWM0OC1kMTkzLTQ0NDYtYjgxNC02NTQ3MGI4NjA1ZjIiLCJyb2xlIjoidXNlciIsImV4cCI6MTc3MDk4NDYxNCwiaWF0IjoxNzcwMzc5ODE0fQ.PNyunNGElUfQfH9wZpeWp1jKcC9pil9r7Dig6zl0kmk"
        })
        .build()

    // 3. Generación de Retrofit inyectando el cliente
    private fun createRetrofit(baseUrl: String): Retrofit {
        // Interceptor para ver las peticiones HTTP
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
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