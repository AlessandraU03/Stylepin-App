package com.ale.stylepin.core.di

import android.content.Context
import android.content.SharedPreferences
import com.ale.stylepin.BuildConfig
import com.ale.stylepin.core.network.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("stylepin_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(prefs: SharedPreferences): AuthInterceptor {
        return AuthInterceptor { prefs.getString("auth_token", null) }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @StylePinRetrofit
    fun provideStylePinRetrofit(okHttpClient: OkHttpClient): Retrofit {
        // Asegúrate de que BASE_URL_STYLEPIN esté en tu BuildConfig o usa un string directo
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_STYLEPIN)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}