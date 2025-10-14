package com.ai.keyboard.presentation.model

enum class LanguageType(
    val id: String,
    val displayName: String,
    val code: String // optional ISO-like code (useful for APIs)
) {
    ENGLISH("en", "English", "en"),
    SPANISH("es", "Spanish", "es"),
    BANGLA("bn", "Bangla", "bn"),
    FRENCH("fr", "French", "fr"),
    GERMAN("de", "German", "de");

    companion object {
        fun fromDisplayName(name: String): LanguageType? {
            return values().find { it.displayName.equals(name, ignoreCase = true) }
        }
    }
}