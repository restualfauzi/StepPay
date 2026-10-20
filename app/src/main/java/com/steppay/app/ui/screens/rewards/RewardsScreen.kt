package com.steppay.app.ui.screens.rewards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.steppay.app.R
import com.steppay.app.ads.AdBanner
import com.steppay.app.ui.StepPayViewModel
import com.steppay.app.ui.components.BottomNavigationBar
import com.steppay.app.ui.components.RewardCard
import com.steppay.app.ui.components.RewardItem
import com.steppay.app.ui.components.StepPayTopBar
import com.steppay.app.ui.navigation.Screen
import com.steppay.app.ui.theme.BackgroundLight
import com.steppay.app.ui.theme.BorderLight
import com.steppay.app.ui.theme.PrimaryBlue
import com.steppay.app.ui.theme.PrimaryText
import com.steppay.app.ui.theme.SecondaryText
import com.steppay.app.ui.theme.SoftBlueBg
import com.steppay.app.ui.theme.SurfaceWhite
import com.steppay.app.ui.theme.WarningOrange

@Composable
fun RewardsScreen(
    viewModel: StepPayViewModel,
    onNavigate: (Screen) -> Unit,
    onRewardSelect: (RewardItem) -> Unit
) {
    val categoryCash = stringResource(R.string.category_cash)

    val rewardsList = listOf(
        RewardItem("1", "PayPal $1", 100000, categoryCash, "", R.drawable.paypal),
        RewardItem("2", "PayPal $5", 500000, categoryCash, "", R.drawable.paypal),
        RewardItem("3", "PayPal $10", 1000000, categoryCash, "", R.drawable.paypal),
        RewardItem("4", "PayPal $20", 2000000, categoryCash, "", R.drawable.paypal)
    )

    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Scaffold(
        topBar = {
            StepPayTopBar(
                title = stringResource(R.string.rewards_title),
                backgroundColor = SurfaceWhite
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Screen.Rewards.route,
                onNavigate = onNavigate
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        com.steppay.app.ui.components.SwipeToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshData() },
            modifier = Modifier.padding(paddingValues)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
            // Header Title
            item(span = { GridItemSpan(2) }) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "PayPal Cash Out",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        letterSpacing = (-0.4).sp
                    )
                    Text(
                        text = "Redeem your coins for real PayPal cash rewards! Make sure your PayPal email is correct during checkout.",
                        fontSize = 13.sp,
                        color = SecondaryText,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "SELECT CASHOUT AMOUNT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryText,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Reward Options Grid ($1, $5, $10, $20)
            items(rewardsList) { reward ->
                RewardCard(
                    reward = reward,
                    onClick = { onRewardSelect(reward) }
                )
            }

            // Information Section 1: How to Earn Faster
            item(span = { GridItemSpan(2) }) {
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Tips",
                                tint = WarningOrange,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tips to Earn Coins Faster",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                        }

                        InfoBulletItem(
                            icon = Icons.AutoMirrored.Filled.DirectionsRun,
                            title = "Daily Step Target",
                            description = "Earn coins for every step you take. Reach your daily step goal to claim bonus coins!"
                        )

                        InfoBulletItem(
                            icon = Icons.Default.Stars,
                            title = "Tapjoy & Offerwall Tasks",
                            description = "Complete game and app offers in the Bonus menu to earn up to 50,000 Coins ($5.00 USD)!"
                        )

                        InfoBulletItem(
                            icon = Icons.Default.PlayCircle,
                            title = "Watch Short Videos & Spin Wheel",
                            description = "Open daily chests, spin the lucky wheel, and watch brief video ads for extra coins."
                        )
                    }
                }
            }

            // Information Section 2: Payout Terms & Conditions
            item(span = { GridItemSpan(2) }) {
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Information",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Payout Terms & Conditions",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                        }

                        InfoTextDetail(
                            title = "1. Accurate PayPal Email",
                            text = "Ensure the PayPal email entered during checkout is active and able to receive USD transfers."
                        )

                        InfoTextDetail(
                            title = "2. Estimated Processing Time",
                            text = "Payout requests are processed and sent within 24 to 48 business hours."
                        )

                        InfoTextDetail(
                            title = "3. Fair Play Policy",
                            text = "Using VPNs, proxies, or multiple accounts is strictly prohibited and will lead to account suspension."
                        )
                    }
                }
            }

            // Ad Banner at Bottom
            item(span = { GridItemSpan(2) }) {
                Column {
                    AdBanner(modifier = Modifier.padding(top = 8.dp))
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
}

@Composable
fun InfoBulletItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(SoftBlueBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PrimaryBlue,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = SecondaryText,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun InfoTextDetail(
    title: String,
    text: String
) {
    Column {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryText
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = SecondaryText,
            lineHeight = 16.sp
        )
    }
}
