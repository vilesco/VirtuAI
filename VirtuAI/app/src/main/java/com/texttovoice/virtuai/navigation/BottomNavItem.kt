package com.texttovoice.virtuai.navigation

import com.texttovoice.virtuai.R


sealed class BottomNavItem(
    val title: Int,
    val icon: Int,
    val icon_filled: Int,
    val route: String
) {
    object Chat : BottomNavItem(
        title = R.string.chat,
        icon = R.drawable.chat,
        icon_filled = R.drawable.chat_filled,
        route = Screen.StartChat.route
    )

    object GenerateImage : BottomNavItem(
        title = R.string.image,
        icon = R.drawable.image,
        icon_filled = R.drawable.image_bold,
        route = Screen.GenerateImage.route
    )

    object TextToSpeech : BottomNavItem(
        title = R.string.audio,
        icon = R.drawable.text_to_speech,
        icon_filled = R.drawable.text_to_speech_bold,
        route = Screen.TextToSpeech.route
    )

    object History : BottomNavItem(
        title = R.string.history,
        icon = R.drawable.time_circle,
        icon_filled = R.drawable.time_circle_filled,
        route = Screen.History.route
    )

    object Settings : BottomNavItem(
        title = R.string.settings,
        icon = R.drawable.setting,
        icon_filled = R.drawable.setting_filled,
        route = Screen.Settings.route
    )
}
