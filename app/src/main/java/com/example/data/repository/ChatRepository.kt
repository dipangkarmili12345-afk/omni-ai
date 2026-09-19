package com.example.data.repository

import com.example.data.local.ChatDao
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatSessionEntity
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class ChatRepository(private val chatDao: ChatDao) {

    val allSessions: Flow<List<ChatSession>> = chatDao.getAllSessions().map { entities ->
        entities.map {
            ChatSession(
                sessionId = it.sessionId,
                title = it.title,
                modelId = it.modelId,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
                messageCount = it.messageCount
            )
        }
    }

    fun getMessagesForSession(sessionId: String): Flow<List<ChatMessage>> {
        return chatDao.getMessagesForSession(sessionId).map { list ->
            list.map {
                ChatMessage(
                    id = it.id,
                    sessionId = it.sessionId,
                    role = it.role,
                    modelId = it.modelId,
                    content = it.content,
                    timestamp = it.timestamp,
                    imageUrl = it.imageUrl
                )
            }
        }
    }

    suspend fun createNewSession(title: String, modelId: String): String {
        val sessionId = UUID.randomUUID().toString()
        val session = ChatSessionEntity(
            sessionId = sessionId,
            title = title,
            modelId = modelId,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            messageCount = 0
        )
        chatDao.insertSession(session)
        return sessionId
    }

    suspend fun saveMessage(
        sessionId: String,
        role: String,
        modelId: String,
        content: String,
        imageUrl: String? = null
    ): ChatMessage {
        val messageId = UUID.randomUUID().toString()
        val entity = ChatMessageEntity(
            id = messageId,
            sessionId = sessionId,
            role = role,
            modelId = modelId,
            content = content,
            timestamp = System.currentTimeMillis(),
            imageUrl = imageUrl
        )
        chatDao.insertMessage(entity)
        chatDao.updateSessionTimestampAndCount(sessionId, System.currentTimeMillis())

        // If it's the first message and title is generic, update title from user prompt
        if (role == "user") {
            val session = chatDao.getSessionById(sessionId)
            if (session != null && (session.title == "New Chat" || session.title.isBlank())) {
                val shortTitle = if (content.length > 32) content.take(30) + "..." else content
                chatDao.updateSessionTitle(sessionId, shortTitle)
            }
        }

        return ChatMessage(
            id = messageId,
            sessionId = sessionId,
            role = role,
            modelId = modelId,
            content = content,
            timestamp = entity.timestamp,
            imageUrl = imageUrl
        )
    }

    suspend fun deleteSession(sessionId: String) {
        chatDao.deleteSession(sessionId)
    }

    suspend fun clearAllHistory() {
        chatDao.deleteAllSessions()
    }
}
