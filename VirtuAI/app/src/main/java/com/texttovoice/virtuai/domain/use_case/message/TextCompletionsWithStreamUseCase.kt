package com.texttovoice.virtuai.domain.use_case.message

import com.texttovoice.virtuai.data.model.TextCompletionsParam
import com.texttovoice.virtuai.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class TextCompletionsWithStreamUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(scope: CoroutineScope, params: TextCompletionsParam) =
        chatRepository.textCompletionsWithStream(scope, params)
}