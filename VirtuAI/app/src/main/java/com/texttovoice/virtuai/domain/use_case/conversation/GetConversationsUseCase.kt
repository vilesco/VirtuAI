package com.texttovoice.virtuai.domain.use_case.conversation

import com.texttovoice.virtuai.domain.repository.ConversationRepository
import javax.inject.Inject

class GetConversationsUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke() =
        conversationRepository.getConversations()
}