package com.texttovoice.virtuai.data.repository

import com.texttovoice.virtuai.data.model.ConversationModel
import com.texttovoice.virtuai.data.source.local.ConversAIDao
import com.texttovoice.virtuai.domain.repository.ConversationRepository
import javax.inject.Inject


class ConversationRepositoryImpl @Inject constructor(
    private val conversAIDao: ConversAIDao

) : ConversationRepository {
    override suspend fun getConversations(): MutableList<ConversationModel> =
        conversAIDao.getConversations()

    override suspend fun getConversation(conversationId: String) =
        conversAIDao.getConversation(conversationId)


    override suspend fun addConversation(conversation: ConversationModel) =
        conversAIDao.addConversation(conversation)

    override suspend fun deleteConversation(conversationId: String) {
        conversAIDao.deleteConversation(conversationId)
        conversAIDao.deleteMessages(conversationId)
    }


    override suspend fun deleteAllConversation() = conversAIDao.deleteAllConversation()

}