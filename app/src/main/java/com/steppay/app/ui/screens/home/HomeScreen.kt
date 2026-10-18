package com.steppay.app.ui.screens.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.steppay.app.R
import com.steppay.app.ads.AdBanner
import com.steppay.app.domain.reward.RewardEngine
import com.steppay.app.ui.StepPayViewModel
import com.steppay.app.ui.components.BottomNavigationBar
import com.steppay.app.ui.components.CoinBalanceCard
import com.steppay.app.ui.components.PrimaryButton
import com.steppay.app.ui.navigation.Screen
import com.steppay.app.ui.theme.BackgroundLight
import com.steppay.app.ui.theme.BorderLight
import com.steppay.app.ui.theme.PrimaryBlue
import com.steppay.app.ui.theme.PrimaryText
import com.steppay.app.ui.theme.SecondaryText
import com.steppay.app.ui.theme.SurfaceWhite
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: StepPayViewModel,
    onNavigate: (Screen) -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()

    val totalCoins = userProfile?.totalCoins ?: 0
    val rupiahValue = RewardEngine.calculateRupiahValue(totalCoins)
    val todaySteps = userProfile?.todaySteps ?: 0
    val targetSteps = userProfile?.dailyTargetSteps ?: 5000

    var currentSteps by remember { mutableIntStateOf(0) }
    LaunchedEffect(todaySteps) {
        currentSteps = todaySteps
    }
    val animatedSteps by animateIntAsState(
        targetValue = currentSteps,
        animationSpec = tween(durationMillis = 1500),
        label = "stepsAnimation"
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Screen.Home.route,
                onNavigate = onNavigate
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header Greeting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (!userProfile?.photoUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(userProfile?.photoUrl)
                                    .crossfade(true)
                                    .error(R.drawable.ic_avatar_boy)
                                    .placeholder(R.drawable.ic_avatar_boy)
                                    .build(),
                                contentDescription = "User Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, BorderLight, CircleShape)
                                    .clickable { onNavigate(Screen.Profile) }
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_avatar_boy),
                                contentDescription = "User Avatar",
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, BorderLight, CircleShape)
                                    .clickable { onNavigate(Screen.Profile) }
                            )
                        }

                        Column {
                            Text(
                                text = stringResource(R.string.greeting_user, userProfile?.name ?: "Restu"),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText,
                                letterSpacing = (-0.6).sp
                            )
                            Text(
                                text = stringResource(R.string.greeting_subtitle),
                                fontSize = 13.sp,
                                color = SecondaryText,
                                letterSpacing = (-0.2).sp
                            )
                        }
                    }

                    // Notification Button
                    Box {
                        IconButton(
                            onClick = { onNavigate(Screen.Info) },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(SurfaceWhite)
                                .border(1.dp, BorderLight, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notification",
                                tint = PrimaryText,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue)
                                .align(Alignment.TopEnd)
                        )
                    }
                }

                // Step Progress Indicator
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(22.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val progress = (animatedSteps.toFloat() / targetSteps.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                        val animatedProgress by animateFloatAsState(
                            targetValue = progress,
                            animationSpec = tween(durationMillis = 1500),
                            label = "progressAnimation"
                        )

                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.size(210.dp),
                            color = BorderLight,
                            strokeWidth = 14.dp,
                            strokeCap = StrokeCap.Round
                        )
                        CircularProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.size(210.dp),
                            color = PrimaryBlue,
                            strokeWidth = 14.dp,
                            strokeCap = StrokeCap.Round
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                                contentDescription = "Walk",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = String.format(Locale.getDefault(), "%,d", animatedSteps).replace(',', '.'),
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText,
                                letterSpacing = (-1).sp
                            )
                            Text(
                                text = "/ ${String.format(Locale.getDefault(), "%,d", targetSteps).replace(',', '.')} ${stringResource(R.string.steps_unit)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = SecondaryText
                            )
                        }
                    }
                }

                // Running Text Announcement Ticker Card (In the middle, with rounded corners & padding)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = "Announcement",
                                tint = SurfaceWhite,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "LIVE",
                                color = SurfaceWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        val tickerText = remember(totalCoins, userProfile?.name) {
                            val formattedCoins = String.format(Locale.getDefault(), "%,d", totalCoins).replace(',', '.')
                            val usdVal = String.format(Locale.US, "%.2f", totalCoins / 10000.0)
                            val rawName = userProfile?.name ?: "Restu"
                            val maskedUser = maskUserName(rawName)
                            "      💰 Total Earned: $formattedCoins Coins (≈ $$usdVal USD)  •  ⚡ Rate: $1.00 USD = 10,000 Coins  •  🎉 @$maskedUser earned $formattedCoins Coins!  •  💵 @B**** just cashed out $5.00 via PayPal!  •  🏃 @S**** earned +150 Coins from Walking!  •  🎁 Complete Tapjoy & Daily Tasks for up to 50,000 Coins!  •  👟 Keep walking & convert your steps into real cash!      "
                        }

                        Text(
                            text = tickerText,
                            color = SurfaceWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.basicMarquee(
                                iterations = Int.MAX_VALUE
                            ),
                            maxLines = 1
                        )
                    }
                }

                // Empty State Hint
                if (animatedSteps == 0) {
                    Text(
                        text = "No steps recorded today.\nStart walking to earn coins!",
                        fontSize = 13.sp,
                        color = SecondaryText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Coin Balance Card
                CoinBalanceCard(
                    totalCoins = totalCoins,
                    rupiahValue = rupiahValue,
                    onHistoryClick = { onNavigate(Screen.History) }
                )

                // Primary Blue Action Button
                PrimaryButton(
                    text = stringResource(R.string.start_now),
                    icon = Icons.Default.PlayArrow,
                    onClick = { onNavigate(Screen.ActivityTracking) }
                )

                // 4 Shortcut Tiles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ShortcutItem(
                        title = stringResource(R.string.shortcut_rewards),
                        iconRes = R.drawable.gift,
                        onClick = { onNavigate(Screen.Rewards) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    ShortcutItem(
                        title = stringResource(R.string.shortcut_withdraw),
                        iconRes = R.drawable.wallet,
                        onClick = { onNavigate(Screen.Withdrawal) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    ShortcutItem(
                        title = stringResource(R.string.shortcut_leaderboard),
                        iconRes = R.drawable.trophy,
                        onClick = { onNavigate(Screen.Leaderboard) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    ShortcutItem(
                        title = "Stats",
                        iconRes = R.drawable.stats,
                        onClick = { onNavigate(Screen.Statistics) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Ad Banner Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "SPONSORED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SecondaryText,
                            modifier = Modifier.align(Alignment.Start),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AdBanner(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // AdMob Banner Ad
                AdBanner(modifier = Modifier.padding(top = 8.dp))

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ShortcutItem(
    title: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 14.dp, horizontal = 4.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText,
                textAlign = TextAlign.Center,
                letterSpacing = (-0.2).sp
            )
        }
    }
}

fun maskUserName(fullName: String): String {
    if (fullName.isBlank()) return "R****"
    val firstWord = fullName.trim().split(" ").firstOrNull() ?: fullName
    if (firstWord.length <= 1) return "${firstWord}****"
    val firstChar = firstWord.first().uppercaseChar()
    val stars = "*".repeat((firstWord.length - 1).coerceAtLeast(4))
    return "$firstChar$stars"
}
