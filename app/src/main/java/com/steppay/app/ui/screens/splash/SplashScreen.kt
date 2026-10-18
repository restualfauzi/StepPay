package com.steppay.app.ui.screens.splash

import android.app.Activity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.steppay.app.R
import com.steppay.app.ads.AdManager
import com.steppay.app.ui.theme.BackgroundLight
import com.steppay.app.ui.theme.PrimaryBlue
import com.steppay.app.ui.theme.PrimaryText
import com.steppay.app.ui.theme.SecondaryText
import com.steppay.app.ui.theme.SoftBlueBg
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var progress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 2000),
        label = "splashProgress"
    )

    LaunchedEffect(Unit) {
        // Preload App Open & Interstitial Ad
        AdManager.loadAppOpenAd(context)
        AdManager.loadInterstitialAd(context)

        // Animate loading slider from 0% to 100%
        progress = 1.0f
        delay(2200)

        // Show App Open Ad if available before proceeding
        if (activity != null) {
            AdManager.showAppOpenAd(activity) {
                onSplashFinished()
            }
        } else {
            onSplashFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.steppay_icon),
                contentDescription = "StepPay Logo",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(24.dp))
            )

            Spacer(modifier = Modifier.height(20.dp))

            // App Name
            Text(
                text = "StepPay",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                letterSpacing = (-0.8).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.app_tagline),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = SecondaryText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Loading Slider Progress Bar
            Column(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = PrimaryBlue,
                    trackColor = SoftBlueBg
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Loading StepPay... ${(animatedProgress * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SecondaryText
                )
            }
        }
    }
}
