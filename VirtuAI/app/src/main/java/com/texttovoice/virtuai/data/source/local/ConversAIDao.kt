package com.texttovoice.virtuai.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.texttovoice.virtuai.common.Constants
import com.texttovoice.virtuai.data.model.ConversationModel
import com.texttovoice.virtuai.data.model.MessageModel

@Dao
interface ConversAIDao {

    @Query(Constants.Queries.GET_CONVERSATIONS)
    suspend fun getConversations(): MutableList<ConversationModel>

    @Query(Constants.Queries.GET_CONVERSATION)
    suspend fun getConversation(id: String): ConversationModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addConversation(conversationModel: ConversationModel)

    @Query(Constants.Queries.DELETE_CONVERSATION)
    suspend fun deleteConversation(id: String)

    @Query(Constants.Queries.DELETE_ALL_CONVERSATION)
    suspend fun deleteAllConversation()

    @Query(Constants.Queries.GET_MESSAGES)
    suspend fun getMessages(conversationId: String): List<MessageModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMessage(messageModel: MessageModel)

    @Query(Constants.Queries.DELETE_MESSAGES)
    suspend fun deleteMessages(conversationId: String)

}