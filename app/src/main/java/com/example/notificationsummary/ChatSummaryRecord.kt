package com.example.notificationsummary

data class ChatSummaryRecord(
	val chatId: String,
	val route: String,
	val sourceCharCount: Int,
	val sourceText: String,
	val summaryText: String?,
	val createdAtMs: Long
)


