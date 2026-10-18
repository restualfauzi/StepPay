package com.steppay.app.domain.model

data class SupportedCountry(
    val code: String,          // ISO 3166-1 alpha-2 (e.g. US)
    val name: String,          // Country Name
    val currencyCode: String,  // ISO 4217 (e.g. USD)
    val currencySymbol: String, // Symbol (e.g. $)
    val flagEmoji: String
) {
    companion object {
        val ALL = listOf(
            SupportedCountry("US", "United States", "USD", "$", "🇺🇸"),
            SupportedCountry("CA", "Canada", "CAD", "$", "🇨🇦"),
            SupportedCountry("GB", "United Kingdom", "GBP", "£", "🇬🇧"),
            SupportedCountry("AU", "Australia", "AUD", "$", "🇦🇺"),
            SupportedCountry("DE", "Germany", "EUR", "€", "🇩🇪"),
            SupportedCountry("FR", "France", "EUR", "€", "🇫🇷"),
            SupportedCountry("NL", "Netherlands", "EUR", "€", "🇳🇱"),
            SupportedCountry("JP", "Japan", "JPY", "¥", "🇯🇵"),
            SupportedCountry("KR", "South Korea", "KRW", "₩", "🇰🇷"),
            SupportedCountry("SG", "Singapore", "SGD", "$", "🇸🇬"),
            SupportedCountry("BR", "Brazil", "BRL", "R$", "🇧🇷"),
            SupportedCountry("ID", "Indonesia", "IDR", "Rp", "🇮🇩")
        )

        fun fromCode(code: String): SupportedCountry {
            return ALL.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ALL.first()
        }
    }
}