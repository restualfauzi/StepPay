package com.steppay.app.domain.model

enum class DistanceUnit(val code: String, val label: String) {
    KILOMETERS("KM", "Kilometers (km)"),
    MILES("MILES", "Miles (mi)");

    fun formatDistance(distanceKm: Double): String {
        return when (this) {
            KILOMETERS -> String.format("%.2f km", distanceKm)
            MILES -> String.format("%.2f mi", distanceKm * 0.621371)
        }
    }

    companion object {
        fun fromCode(code: String): DistanceUnit {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: KILOMETERS
        }
    }
}