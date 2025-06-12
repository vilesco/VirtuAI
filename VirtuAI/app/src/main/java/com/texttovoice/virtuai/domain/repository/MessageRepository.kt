package com.texttovoice.virtuai.domain.repository

import com.texttovoice.virtuai.data.model.MessageModel

interface MessageRepository {
    suspend fun getMessages(conversationId: String): List<MessageModel>
    suspend fun addMessage(message: MessageModel)
    suspend fun deleteMessages(conversationId: String)
}