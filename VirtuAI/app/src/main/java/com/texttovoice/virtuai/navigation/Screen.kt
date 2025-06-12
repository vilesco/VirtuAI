package com.texttovoice.virtuai.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Login : Screen("login_screen")
    object StartChat : Screen("start_chat_screen")
    object Chat : Screen("chat_screen")
    object GenerateImage : Screen("generate_image_screen")
    object TextToSpeech : Screen("text_to_speech_screen")
    object History : Screen("history_screen")
    object Settings : Screen("settings_screen")
    object DeleteHistory : Screen("delete_history_screen")
    object Languages : Screen("languages_screen")
    object Logout : Screen("logout_screen")
    object Upgrade : Screen("upgrade_screen")
    object Widgets : Screen("widgets_screen")
    object ChooseMedia : Screen("choose_media_screen")
    object Camera : Screen("camera_screen")
    object UpgradeInfoDialog : Screen("upgrade_info_dialog_screen")
    object LinkSummarize : Screen("link_summarize_screen")
    object ChooseScanImageType : Screen("choose_scan_type_screen")
    object ChooseGPT : Screen("choose_gpt_screen")
    object ShowImage : Screen("show_image_screen")
}