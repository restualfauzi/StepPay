package com.steppay.app.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Interface abstraction for Map Providers (osmdroid, MapLibre, etc.)
 */
interface MapProvider {
    @Composable
    fun MapViewContainer(
        modifier: Modifier,
        userLocation: Pair<Double, Double>?,
        routePoints: List<Pair<Double, Double>>,
        isTracking: Boolean,
        zoomInTrigger: Int,
        zoomOutTrigger: Int,
        onMapCentered: (() -> Unit)?
    )
}
