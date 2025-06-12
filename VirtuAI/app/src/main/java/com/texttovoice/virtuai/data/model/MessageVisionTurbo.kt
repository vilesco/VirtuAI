package com.texttovoice.virtuai.data.model

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName


sealed class MessageVisionTurboContent {
    data class Text(
        @SerializedName("type")
        var type: String = "text",
        @SerializedName("text")
        var text: String = ""
    ) : MessageVisionTurboContent()

    data class Image(
        @SerializedName("type")
        var type: String = "image_url",
        @SerializedName("image_url")
        var image_url: ImageURL
    ) : MessageVisionTurboContent()

}



data class ImageURL(
    @SerializedName("url")
    var url: String = ""
)

data class MessageVisionTurbo(
    @SerializedName("content")
    var content: List<MessageVisionTurboContent>,
    @SerializedName("role")
    val role: TurboRole = TurboRole.user
)



fun MessageVisionTurbo.toJson(): JsonObject {
    val gson = Gson()
    val json = JsonObject()

    // Serialize the content list
    val contentJsonArray = JsonArray()
    content.forEach { contentItem ->
        when (contentItem) {
            is MessageVisionTurboContent.Text -> contentJsonArray.add(gson.toJsonTree(contentItem))
            is MessageVisionTurboContent.Image -> contentJsonArray.add(gson.toJsonTree(contentItem))
            else -> {}
        }
    }
    json.add("content", contentJsonArray)

    // Serialize the role
    json.addProperty("role", role.value)

    return json
}
