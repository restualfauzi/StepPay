package com.steppay.app.ui.map.osmdroid

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.steppay.app.R
import com.steppay.app.ui.map.MapProvider
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.io.File

class OsmMapProvider : MapProvider {

    @Composable
    override fun MapViewContainer(
        modifier: Modifier,
        userLocation: Pair<Double, Double>?,
        routePoints: List<Pair<Double, Double>>,
        isTracking: Boolean,
        zoomInTrigger: Int,
        zoomOutTrigger: Int,
        onMapCentered: (() -> Unit)?
    ) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current

        // Configure osmdroid user agent & cache directory
        remember {
            val config = Configuration.getInstance()
            config.load(context, context.getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE))
            config.userAgentValue = "StepPay/0.0.1 (com.steppay.app)"
            config.osmdroidBasePath = File(context.cacheDir, "osmdroid")
            config.osmdroidTileCache = File(context.cacheDir, "osmdroid/tiles")
            true
        }

        val mapView = remember {
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(16.5)

                // Visible Attribution Overlay
                val copyrightOverlay = CopyrightOverlay(context)
                overlays.add(copyrightOverlay)
            }
        }

        // Handle Zoom In Action
        LaunchedEffect(zoomInTrigger) {
            if (zoomInTrigger > 0) {
                mapView.controller.zoomIn()
            }
        }

        // Handle Zoom Out Action
        LaunchedEffect(zoomOutTrigger) {
            if (zoomOutTrigger > 0) {
                mapView.controller.zoomOut()
            }
        }

        // Lifecycle management for MapView
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> mapView.onResume()
                    Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                    Lifecycle.Event.ON_DESTROY -> mapView.onDetach()
                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                mapView.onDetach()
            }
        }

        // Update map overlays (polyline, markers, and camera position)
        LaunchedEffect(userLocation, routePoints) {
            // Remove previous polylines & user markers (keep CopyrightOverlay)
            mapView.overlays.removeAll { it !is CopyrightOverlay }

            // 1. Draw Polyline for GPS Route
            if (routePoints.size >= 2) {
                val geoPoints = routePoints.map { GeoPoint(it.first, it.second) }
                val polyline = Polyline(mapView).apply {
                    outlinePaint.color = Color.parseColor("#2563EB") // Vibrant Primary Blue
                    outlinePaint.strokeWidth = 12f
                    setPoints(geoPoints)
                }
                mapView.overlays.add(polyline)

                // Zoom camera to fit route bounding box or center on the route
                try {
                    val boundingBox = BoundingBox.fromGeoPoints(geoPoints)
                    mapView.post {
                        try {
                            mapView.zoomToBoundingBox(boundingBox, true, 48)
                        } catch (_: Exception) {
                            val center = geoPoints.last()
                            mapView.controller.setZoom(16.5)
                            mapView.controller.setCenter(center)
                        }
                    }
                } catch (_: Exception) {
                    val center = geoPoints.last()
                    mapView.controller.setZoom(16.5)
                    mapView.controller.setCenter(center)
                }
            } else if (userLocation != null) {
                val point = GeoPoint(userLocation.first, userLocation.second)
                mapView.controller.setZoom(16.5)
                mapView.controller.setCenter(point)
            } else if (routePoints.isNotEmpty()) {
                val point = GeoPoint(routePoints.first().first, routePoints.first().second)
                mapView.controller.setZoom(16.5)
                mapView.controller.setCenter(point)
            }

            // 2. User Location Marker
            userLocation?.let { (lat, lng) ->
                val point = GeoPoint(lat, lng)
                val userMarker = Marker(mapView).apply {
                    position = point
                    title = "My Location"
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_user_location_dot)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                }
                mapView.overlays.add(userMarker)
            }

            mapView.invalidate()
        }

        AndroidView(
            factory = { mapView },
            modifier = modifier
        )
    }
}
