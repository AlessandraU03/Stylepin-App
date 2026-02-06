package com.ale.stylepin.core.di

import android.content.Context
import com.ale.stylepin.BuildConfig
import com.ale.stylepin.core.network.StylePinApi
import com.ale.stylepin.features.auth.data.repositories.AuthRepositoryImpl
import com.ale.stylepin.features.auth.domain.repositories.AuthRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppContainer(context: Context) {

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
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val stylePinRetrofit = createRetrofit(BuildConfig.BASE_URL_STYLEPIN)

    val stylePinApi: StylePinApi by lazy {
        stylePinRetrofit.create(StylePinApi::class.java)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(stylePinApi)
    }
}