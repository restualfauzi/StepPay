package com.steppay.app.ui.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.steppay.app.ui.map.osmdroid.OsmMapProvider
import com.steppay.app.ui.theme.PrimaryBlue
import com.steppay.app.ui.theme.PrimaryText
import com.steppay.app.ui.theme.SurfaceWhite

@Composable
fun StepPayMapView(
    modifier: Modifier = Modifier,
    userLocation: Pair<Double, Double>?,
    routePoints: List<Pair<Double, Double>> = emptyList(),
    isTracking: Boolean = false,
    mapProvider: MapProvider = remember { OsmMapProvider() },
    onCenterLocationClick: (() -> Unit)? = null
) {
    var zoomInTrigger by remember { mutableIntStateOf(0) }
    var zoomOutTrigger by remember { mutableIntStateOf(0) }

    Box(modifier = modifier) {
        // Map Container provided by MapProvider abstraction
        mapProvider.MapViewContainer(
            modifier = Modifier.fillMaxSize(),
            userLocation = userLocation,
            routePoints = routePoints,
            isTracking = isTracking,
            zoomInTrigger = zoomInTrigger,
            zoomOutTrigger = zoomOutTrigger,
            onMapCentered = onCenterLocationClick
        )

        // GPS / Location Status Banner Overlay if location is null or unavailable
        if (userLocation == null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.75f),
                contentColor = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOff,
                        contentDescription = "GPS Searching",
                        tint = Color(0xFFFFB74D),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Searching for GPS Signal...",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Floating Zoom and Location Control Buttons (Right Side)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Zoom In (+) FAB Button
            SmallFloatingActionButton(
                onClick = { zoomInTrigger++ },
                containerColor = SurfaceWhite,
                contentColor = PrimaryText,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Zoom In"
                )
            }

            // Zoom Out (-) FAB Button
            SmallFloatingActionButton(
                onClick = { zoomOutTrigger++ },
                containerColor = SurfaceWhite,
                contentColor = PrimaryText,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Zoom Out"
                )
            }

            // Center Location FAB Button
            if (userLocation != null && onCenterLocationClick != null) {
                SmallFloatingActionButton(
                    onClick = onCenterLocationClick,
                    containerColor = SurfaceWhite,
                    contentColor = PrimaryBlue,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = "Center Location"
                    )
                }
            }
        }
    }
}
