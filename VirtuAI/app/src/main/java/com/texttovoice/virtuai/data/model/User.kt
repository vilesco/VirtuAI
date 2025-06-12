package com.texttovoice.virtuai.data.model


data class User(
    val id: String,
    val email: String,
    @field:JvmField
    val isProUser: Boolean = false,
    val remainingMessageCount: Int = com.texttovoice.virtuai.common.Constants.Preferences.FREE_MESSAGE_COUNT_DEFAULT,
){
    constructor() : this("", "", false, com.texttovoice.virtuai.common.Constants.Preferences.FREE_MESSAGE_COUNT_DEFAULT)
}
