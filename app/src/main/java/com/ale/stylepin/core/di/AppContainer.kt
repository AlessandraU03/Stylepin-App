package com.ale.stylepin.core.di

import android.content.Context
import com.ale.stylepin.BuildConfig
import com.ale.stylepin.core.network.StylePinApi
import com.ale.stylepin.features.auth.data.repositories.AuthRepositoryImpl
import com.ale.stylepin.features.auth.domain.repositories.AuthRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Usamos la URL de tu local.properties
    private val stylePinRetrofit = createRetrofit(BuildConfig.BASE_URL_STYLEPIN)

    val stylePinApi: StylePinApi by lazy {
        stylePinRetrofit.create(StylePinApi::class.java)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(stylePinApi)
    }
}