package com.texttovoice.virtuai.common

object Constants {
    const val BASE_URL = "https://api.openai.com/v1/"

    const val REWARDED_AD_UNIT_ID = "Place your rewarded ad unit id here"
    const val INTERSTITIAL_AD_UNIT_ID = "Place your interstitial ad unit id here"
    const val BANNER_AD_UNIT_ID = "Place your banner ad unit id here"

    const val PRIVACY_POLICY = "Place your privacy policy link here"
    const val ABOUT = "Place your About link here"
    const val HELP = "Place your help link here"
    const val FEEDBACK = "market://details?id=com.texttovoice.virtuai"

    const val PRODUCT_ID = "virtuai_pro"

    const val WEEKLY_BASE_PLAN = "virtuai-pro"
    const val MONTHLY_BASE_PLAN = "virtuai-pro-month"
    const val YEARLY_BASE_PLAN = "virtuai-pro-year"


    const val TRANSITION_ANIMATION_DURATION = 400
    const val IS_DELETE = "is_delete"
    const val MEDIA_SOURCE = "media_source"
    const val OCR = "ocr"
    const val GPT = "gpt"
    const val DEFAULT_MESSAGE = "default_message"
    const val LINK_SUMMARIZE = "link_summarize"
    const val LINK_SUMMARIZE_CONTENT = "link_summarize_content"
    const val DEFAULT_GPT_MODEL = "3.5"
    const val START_WEB_LINK = "summarizeWebPage||"

    const val WEB_CLIENT_ID =
        "1003103976137-9jhdqao0j2cmpsjv09dnjculogo7a6oi.apps.googleusercontent.com"

    const val SIZE_256 = "256x256"
    const val SIZE_512 = "512x512"
    const val SIZE_1024 = "1024x1024"
    const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1

    object Prompts {
        const val REALISTIC = "rendered in a highly realistic style"
        const val CARTOON = "in a bright and colorful cartoon style"
        const val PENCIL_SKETCH = "as a detailed pencil sketch"
        const val OIL_PAINTING = "in the style of a classical oil painting"
        const val WATER_COLOR = "with a delicate watercolor effect"
        const val POP_ART = "in a vibrant pop art style"
        const val SURREALIST = "in a surrealistic style, with dream-like elements"
        const val PIXEL_ART = "as pixel art in a digital 8-bit style"
        const val NOUVEAU = "in an Art Nouveau style with elegant lines and floral patterns"
        const val ABSTRACT_ART = "in an abstract style with bold shapes and colors"
    }

    object Preferences {
        const val TEXT_TO_SPEECH = "textToSpeech"
        const val TEXT_TO_SPEECH_FIRST_TIME = "textToSpeechFirstTime"
        const val LANGUAGE_CODE = "languageCode"
        const val LANGUAGE_NAME = "languageName"
        const val SHARED_PREF_NAME = "mova_shared_pref"
        const val DARK_MODE = "darkMode"
        const val PRO_VERSION = "proVersion"
        const val FIRST_TIME = "firstTime"
        const val API_KEY = "api_key"
        const val GPT_MODEL = "gptModel"
        const val FREE_MESSAGE_COUNT = "freeMessageCount"
        const val FREE_MESSAGE_LAST_CHECKED_TIME = "freeMessageLastCheckedTime"
        const val FREE_MESSAGE_COUNT_DEFAULT = 99
        const val INCREASE_MESSAGE_COUNT = 1
    }

    object Queries {
        const val GET_CONVERSATIONS = "SELECT * FROM conversations ORDER BY createdAt DESC"
        const val GET_CONVERSATION = "SELECT * FROM conversations WHERE id = :id"
        const val DELETE_CONVERSATION = "DELETE FROM conversations WHERE id = :id"
        const val DELETE_ALL_CONVERSATION = "DELETE FROM conversations"
        const val DELETE_MESSAGES = "DELETE FROM messages WHERE conversationId = :conversationId"
        const val GET_MESSAGES =
            "SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY createdAt DESC"

    }

    object Endpoints {
        const val TEXT_COMPLETIONS = "completions"
        const val TEXT_COMPLETIONS_TURBO = "chat/completions"
        const val MODERATIONS = "moderations"
        const val GENERATE_IMAGE = "images/generations"
        const val CREATE_SPEECH = "audio/speech"

    }

    const val DEFAULT_AI =
        "You are an AI model that created by VirtuAI. if someone asked this, answer it."


}