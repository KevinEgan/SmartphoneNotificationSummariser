package com.example.notificationsummary
import kotlinx.serialization.Serializable

@Serializable
// This class represents notifications and their data fields
data class NotificationData(
    val capturedAtMs: Long,
    val postTimeMs: Long,
    val packageName: String,
    val groupKey: String? = null,
    val isGroupSummary: Boolean = false,
    val title: String?,
    val text: String?,
    val bigText: String?,
    val subText: String?,
    val category: String?
)