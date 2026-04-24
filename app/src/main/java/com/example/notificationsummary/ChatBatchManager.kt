package com.example.notificationsummary

import android.util.Log

/*
Manages in-memory chat batches grouped by chatId.
Appends messages and tracks character count to decide local vs cloud summarisation routes
*/
class ChatBatchManager(private val maxLocalChars: Int = 2000) {

    // Map of chatId -> accumulated message content
    private val chatBatches: MutableMap<String, StringBuilder> = mutableMapOf()


    // Append a message to the batch for a given chatId and return it
    fun appendMessage(chatId: String, messageText: String?): ChatBatchState {
        val currentBuilder = chatBatches.getOrPut(chatId) {
            StringBuilder()
        }
        currentBuilder.append(messageText)

        return getCurrentBatchState(chatId)
    }


    // Get the current state of a batch

    private fun getCurrentBatchState(chatId: String): ChatBatchState {
        val content = chatBatches[chatId]?.toString() ?: ""
        val charCount = content.length
        val route = if (charCount <= maxLocalChars) {
            BatchRoute.LOCAL.name
        } else {
            BatchRoute.CLOUD_DEFERRED.name
        }

        return ChatBatchState(
            chatId = chatId,
            content = content,
            charCount = charCount,
            route = route
        )
    }


    // Get all current batch states by taking every chat ID in the map and converting it to a data object
    fun getAllBatchStates(): List<ChatBatchState> {
        val states = mutableListOf<ChatBatchState>()
        for (chatId in chatBatches.keys) {
            states.add(getCurrentBatchState(chatId))
        }
        return states
    }

    // Clear all batch states
    fun clearAllBatchStates(){
        chatBatches.clear()
        Log.d("*****BATCH", "Cleared all chat batches")
    }

    // Get batch for a specific chat
    fun getBatchState(chatId: String): ChatBatchState {
        val state = getCurrentBatchState(chatId)
        return state
    }


    // Check if a chat batch exceeds the local limit if we add more text
    fun wouldExceedLimit(): Boolean {
        return true
    }
}

