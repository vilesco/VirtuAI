package com.texttovoice.virtuai.domain.repository

import com.texttovoice.virtuai.data.model.ConversationModel

interface ConversationRepository {
    suspend fun getConversations(): MutableList<ConversationModel>
    suspend fun getConversation(conversationId: String): ConversationModel
    suspend fun addConversation(conversation: ConversationModel)
    suspend fun deleteConversation(conversationId: String)
    suspend fun deleteAllConversation()
}