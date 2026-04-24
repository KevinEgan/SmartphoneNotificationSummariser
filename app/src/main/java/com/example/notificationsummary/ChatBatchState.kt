package com.example.notificationsummary

import kotlinx.serialization.Serializable

enum class BatchRoute {
    LOCAL,
    CLOUD_DEFERRED
}

@Serializable
data class ChatBatchState(
    val chatId: String,
    val content: String,
    val charCount: Int,
    val route: String
)

