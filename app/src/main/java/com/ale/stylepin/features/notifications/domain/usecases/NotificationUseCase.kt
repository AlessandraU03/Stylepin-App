package com.ale.stylepin.features.notifications.domain.usecases

import com.ale.stylepin.features.notifications.domain.entities.Notification
import com.ale.stylepin.features.notifications.domain.repository.NotificationsRepository
import javax.inject.Inject

class SendFCMTokenUseCase @Inject constructor(
    private val notificationsRepository: NotificationsRepository
) {
    
    suspend fun execute(token: String, deviceName: String) {
        println("📤 Enviando FCM Token al backend...")
        
        try {
            val response = notificationsRepository.saveFCMToken(token, deviceName)
            
            if (response.isSuccessful) {
                println("✅ FCM Token guardado en el servidor")
                println("   Response: ${response.body()}")
            } else {
                println("❌ Error al guardar FCM Token: ${response.code()}")
                println("   Message: ${response.message()}")
                throw Exception("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            println("❌ Excepción al enviar FCM Token: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}

// ✅ MANTENER: GetNotificationsUseCase
class GetNotificationsUseCase @Inject constructor(
    private val repository: NotificationsRepository
) {
    suspend operator fun invoke(): Result<List<Notification>> {
        return repository.getNotifications()
    }
}