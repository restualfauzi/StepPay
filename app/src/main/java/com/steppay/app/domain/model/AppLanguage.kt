package com.steppay.app.domain.model

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    SPANISH("es", "Español"),
    PORTUGUESE("pt", "Português"),
    GERMAN("de", "Deutsch"),
    FRENCH("fr", "Français"),
    JAPANESE("ja", "日本語"),
    KOREAN("ko", "한국어"),
    INDONESIAN("id", "Bahasa Indonesia");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
        }
    }
}