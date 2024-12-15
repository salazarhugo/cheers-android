package com.salazar.cheers.core.model


enum class Language(
    val value: String,
    val nameInEnglish: String,
    val selected: Boolean = false,
) {
    ENGLISH(
        value = "English",
        nameInEnglish = "English",
        selected = true,
    ),
    FRENCH(
        value = "Français",
        nameInEnglish = "French",
    ),
    SPANISH(
        value = "Español",
        nameInEnglish = "Spanish",
    ),
    PORTUGUESE(
        value = "Português",
        nameInEnglish = "Portuguese",
    ),
    ITALIAN(
        value = "Italiano",
        nameInEnglish = "Italian",
    ),
    GERMAN(
        value = "Deutsch",
        nameInEnglish = "German",
    ),
    RUSSIAN(
        value = "Русский",
        nameInEnglish = "Russian",
    ),
    CHINESE(
        value = "中文",
        nameInEnglish = "Chinese",
    ),
    JAPANESE(
        value = "日本語",
        nameInEnglish = "Japanese",
    ),
    KOREAN(
        value = "한국어",
        nameInEnglish = "Korean",
    ),
    ARABIC(
        value = "العربية",
        nameInEnglish = "Arabic",
    ),
    HEBREW(
        value = "עברית",
        nameInEnglish = "Hebrew",
    ),
    TURKISH(
        value = "Türkçe",
        nameInEnglish = "Turkish",
    ),
    INDONESIAN(
        value = "Bahasa Indonesia",
        nameInEnglish = "Indonesian",
    ),
    THAI(
        value = "ไทย",
        nameInEnglish = "Thai",
    ),
    VIETNAMESE(
        value = "Tiếng Việt",
        nameInEnglish = "Vietnamese",
    ),
    POLISH(
        value = "Polski",
        nameInEnglish = "Polish",
    ),
    ROMANIAN(
        value = "Română",
        nameInEnglish = "Romanian",
    ),
    UKRAINIAN(
        value = "Українська",
        nameInEnglish = "Ukrainian",
    ),
    CATALAN(
        value = "Català",
        nameInEnglish = "Catalan",
    ),
    CROATIAN(
        value = "Hrvatski",
        nameInEnglish = "Croatian",
    ),
    CZECH(
        value = "Čeština",
        nameInEnglish = "Czech",
    ),
    DANISH(
        value = "Dansk",
        nameInEnglish = "Danish",
    ),
    FINNISH(
        value = "Suomi",
        nameInEnglish = "Finnish",
    ),
}