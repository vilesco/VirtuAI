package com.texttovoice.virtuai.domain.use_case.conversation

import com.texttovoice.virtuai.data.model.ConversationModel
import com.texttovoice.virtuai.domain.repository.ConversationRepository
import javax.inject.Inject

class CreateConversationUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(conversation: ConversationModel) =
        conversationRepository.addConversation(conversation)
}