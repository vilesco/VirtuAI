package com.texttovoice.virtuai.domain.use_case.image

import com.texttovoice.virtuai.data.model.RequestBody
import com.texttovoice.virtuai.domain.repository.ImageRepository
import javax.inject.Inject

class GenerateImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend  operator fun invoke(requestBody: RequestBody) = imageRepository.generateImage(requestBody)
}