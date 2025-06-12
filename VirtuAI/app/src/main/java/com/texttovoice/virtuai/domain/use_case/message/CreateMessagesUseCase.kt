package com.texttovoice.virtuai.domain.use_case.message

import com.texttovoice.virtuai.data.model.MessageModel
import com.texttovoice.virtuai.domain.repository.MessageRepository
import javax.inject.Inject

class CreateMessagesUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(message: MessageModel) =
        messageRepository.addMessage(message)
}