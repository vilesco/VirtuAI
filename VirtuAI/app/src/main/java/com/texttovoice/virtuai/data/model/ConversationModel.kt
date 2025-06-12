package com.texttovoice.virtuai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "conversations")
data class ConversationModel(
    @PrimaryKey(autoGenerate = false)
    var id: String = Date().time.toString(),
    var title: String = "",
    var model: GPTModel = GPTModel.gpt35Turbo,
    var createdAt: String = Calendar.getInstance().time.toString()
)