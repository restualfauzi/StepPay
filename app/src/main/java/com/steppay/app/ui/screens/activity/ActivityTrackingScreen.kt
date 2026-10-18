package com.steppay.app.ui.screens.activity

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.steppay.app.R
import com.steppay.app.ads.AdManager
import com.steppay.app.ui.StepPayViewModel
import com.steppay.app.ui.components.PrimaryButton
import com.steppay.app.ui.map.StepPayMapView
import com.steppay.app.ui.theme.BackgroundLight
import com.steppay.app.ui.theme.BorderLight
import com.steppay.app.ui.theme.BrightBlue
import com.steppay.app.ui.theme.PrimaryBlue
import com.steppay.app.ui.theme.PrimaryText
import com.steppay.app.ui.theme.SecondaryText
import com.steppay.app.ui.theme.SoftBlueBg
import com.steppay.app.ui.theme.SoftGreenBg
import com.steppay.app.ui.theme.SuccessGreen
import com.steppay.app.ui.theme.SurfaceWhite
import com.steppay.app.ui.theme.WarningOrange
import java.util.Locale

@Composable
fun ActivityTrackingScreen(
    viewModel: StepPayViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val trackingState by viewModel.trackingState.collectAsState()
    var isLocked by remember { mutableStateOf(false) }
    var showFinishDialog by remember { mutableStateOf(false) }

    // Request runtime permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    LaunchedEffect(Unit) {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionLauncher.launch(permissions.toTypedArray())

        // Start tracking if not active
        if (!trackingState.isTracking) {
            viewModel.startTracking("Walking")
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(SurfaceWhite)) {
        // 1. Spacious Fullscreen Interactive Map View
        StepPayMapView(
            modifier = Modifier.fillMaxSize(),
            userLocation = trackingState.routePoints.lastOrNull(),
            routePoints = trackingState.routePoints,
            isTracking = trackingState.isTracking && !trackingState.isPaused,
            onCenterLocationClick = {
                // Map auto-centers on user location update
            }
        )

        // 2. Modern Glassmorphic Top Controls Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(
                onClick = {
                    if (trackingState.isTracking) {
                        showFinishDialog = true
                    } else {
                        onBackClick()
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SurfaceWhite)
                    .border(1.dp, BorderLight, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = PrimaryText,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Activity Badge Tag
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SurfaceWhite,
                shadowElevation = 4.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (trackingState.activityType == "Running" || trackingState.activityType == "Lari") "Running" else "Walking",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                }
            }
        }

        // 3. Compact Clean White Bottom Dashboard Sheet Overlay
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .border(1.dp, BorderLight, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Realtime Main Step Counter Number
                val stepDisplay = trackingState.steps
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format(Locale.getDefault(), "%,d", stepDisplay).replace(',', '.'),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = PrimaryText,
                        letterSpacing = (-0.5).sp
                    )

                    Text(
                        text = stringResource(R.string.steps_unit).uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        color = SecondaryText,
                        letterSpacing = 1.5.sp
                    )
                }

                // 3 Realtime Stat Cards (Distance | Time | Calories)
                val distDisplay = trackingState.distanceKm
                val seconds = trackingState.durationSeconds
                val timeStr = String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60)
                val calDisplay = trackingState.calories

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatBox(
                        icon = Icons.Default.LocationOn,
                        value = String.format(Locale.getDefault(), "%.2f", distDisplay),
                        unit = "km",
                        accentColor = PrimaryBlue,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        icon = Icons.Default.Timer,
                        value = timeStr,
                        unit = stringResource(R.string.time),
                        accentColor = PrimaryBlue,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        icon = Icons.Default.Whatshot,
                        value = "$calDisplay",
                        unit = stringResource(R.string.calories),
                        accentColor = WarningOrange,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Controls Row: Lock | Play/Pause | Stop
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Lock Button
                    IconButton(
                        onClick = { isLocked = !isLocked },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(BackgroundLight)
                            .border(1.dp, BorderLight, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = if (isLocked) stringResource(R.string.unlock) else stringResource(R.string.lock),
                            tint = PrimaryText
                        )
                    }

                    // Main Big Play/Pause Button
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(PrimaryBlue, BrightBlue)
                                )
                            )
                            .clickable {
                                if (!isLocked) {
                                    if (trackingState.isPaused) {
                                        viewModel.resumeTracking()
                                    } else {
                                        viewModel.pauseTracking()
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (trackingState.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (trackingState.isPaused) stringResource(R.string.resume) else stringResource(R.string.pause),
                            tint = SurfaceWhite,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    // Stop Workout Button
                    IconButton(
                        onClick = {
                            if (!isLocked) {
                                showFinishDialog = true
                            }
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(BackgroundLight)
                            .border(1.dp, BorderLight, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = stringResource(R.string.finish),
                            tint = PrimaryText
                        )
                    }
                }

                // Bottom Earned Coin Banner Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                        .clickable { showFinishDialog = true },
                    colors = CardDefaults.cardColors(containerColor = SoftBlueBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.dolar),
                                contentDescription = "Coin Icon",
                                modifier = Modifier.size(36.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                val coins = trackingState.coinsEarned
                                Text(
                                    text = "+ $coins coins",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                                Text(
                                    text = stringResource(R.string.greeting_subtitle),
                                    fontSize = 12.sp,
                                    color = SecondaryText
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = SecondaryText
                        )
                    }
                }
            }
        }
    }

    // Modern Elegant iOS-Style Complete Workout Dialog Modal
    if (showFinishDialog) {
        Dialog(onDismissRequest = { showFinishDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                color = SurfaceWhite,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Celebration Header Icon
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(SoftBlueBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Title & Subtitle
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.finish_dialog_title),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText,
                            letterSpacing = (-0.4).sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.finish_dialog_msg),
                            fontSize = 13.sp,
                            color = SecondaryText,
                            textAlign = TextAlign.Center
                        )
                    }

                    // 2x2 Grid Summary Stat Cards
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            WorkoutStatTile(
                                label = stringResource(R.string.steps),
                                value = String.format(Locale.getDefault(), "%,d", trackingState.steps).replace(',', '.'),
                                modifier = Modifier.weight(1f)
                            )
                            WorkoutStatTile(
                                label = stringResource(R.string.distance),
                                value = "${String.format(Locale.getDefault(), "%.2f", trackingState.distanceKm)} km",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            WorkoutStatTile(
                                label = stringResource(R.string.time),
                                value = "${trackingState.durationSeconds / 60}m ${trackingState.durationSeconds % 60}s",
                                modifier = Modifier.weight(1f)
                            )
                            WorkoutStatTile(
                                label = stringResource(R.string.calories),
                                value = "${trackingState.calories} kcal",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Reward Coin Bonus Banner
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = SoftGreenBg)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.dolar),
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "+${trackingState.coinsEarned} Coins Earned!",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Action Buttons (Save & Cancel)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PrimaryButton(
                            text = stringResource(R.string.save),
                            height = 48.dp,
                            onClick = {
                                showFinishDialog = false
                                val activity = context as? Activity
                                if (activity != null) {
                                    AdManager.showInterstitialAd(activity)
                                }
                                viewModel.finishAndSaveActivity {
                                    onBackClick()
                                }
                            }
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showFinishDialog = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.cancel),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = SecondaryText
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutStatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(BackgroundLight)
            .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = SecondaryText,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                color = PrimaryText,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StatBox(
    icon: ImageVector,
    value: String,
    unit: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(BackgroundLight)
            .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = unit,
                fontSize = 11.sp,
                color = SecondaryText
            )
        }
    }
}
