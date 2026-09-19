package com.example.data.model

data class ChatMessage(
    val id: String,
    val sessionId: String,
    val role: String, // "user" or "model"
    val modelId: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUrl: String? = null,
    val isStreaming: Boolean = false
)

data class ChatSession(
    val sessionId: String,
    val title: String,
    val modelId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val messageCount: Int = 0
)
