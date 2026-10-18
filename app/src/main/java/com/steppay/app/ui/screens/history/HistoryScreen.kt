package com.steppay.app.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.steppay.app.R
import com.steppay.app.data.local.entity.ActivitySessionEntity
import com.steppay.app.data.local.entity.WithdrawalEntity
import com.steppay.app.ui.StepPayViewModel
import com.steppay.app.ui.components.BottomNavigationBar
import com.steppay.app.ui.components.StepPayTopBar
import com.steppay.app.ui.components.TransactionItem
import com.steppay.app.ui.map.StepPayMapView
import com.steppay.app.ui.navigation.Screen
import com.steppay.app.ui.theme.BackgroundLight
import com.steppay.app.ui.theme.BorderLight
import com.steppay.app.ui.theme.PrimaryBlue
import com.steppay.app.ui.theme.PrimaryText
import com.steppay.app.ui.theme.SecondaryText
import com.steppay.app.ui.theme.SoftBlueBg
import com.steppay.app.ui.theme.SoftGreenBg
import com.steppay.app.ui.theme.SoftPinkBg
import com.steppay.app.ui.theme.SuccessGreen
import com.steppay.app.ui.theme.SurfaceWhite
import com.steppay.app.ui.theme.WarningOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: StepPayViewModel,
    onNavigate: (Screen) -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Coins, 1: Payouts, 2: Activity

    val transactions by viewModel.allTransactions.collectAsState()
    val withdrawals by viewModel.allWithdrawals.collectAsState()
    val sessions by viewModel.allSessions.collectAsState()

    val tabTitleCoins = stringResource(R.string.total_coins)
    val tabTitlePayout = stringResource(R.string.shortcut_withdraw)
    val tabTitleActivity = stringResource(R.string.nav_activity)

    Scaffold(
        topBar = {
            StepPayTopBar(
                title = stringResource(R.string.nav_history),
                onBackClick = onBackClick,
                backgroundColor = SurfaceWhite
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Screen.History.route,
                onNavigate = onNavigate
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // iOS Segmented Control Header Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BorderLight)
                    .padding(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TabItem(
                        title = tabTitleCoins,
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    TabItem(
                        title = tabTitlePayout,
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                    TabItem(
                        title = tabTitleActivity,
                        isSelected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(transactions) { trx ->
                            TransactionItem(transaction = trx)
                            HorizontalDivider(
                                color = BorderLight,
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
                1 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(withdrawals) { withdrawal ->
                            WithdrawalHistoryItem(withdrawal = withdrawal)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(sessions) { session ->
                            ActivitySessionHistoryItem(
                                session = session,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) SurfaceWhite else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = PrimaryText,
            letterSpacing = (-0.2).sp
        )
    }
}

@Composable
fun ActivitySessionHistoryItem(
    session: ActivitySessionEntity,
    viewModel: StepPayViewModel,
    modifier: Modifier = Modifier
) {
    val routePoints = remember(session.routeJson) {
        viewModel.parseRouteJson(session.routeJson)
    }
    val dateFormatted = remember(session.timestamp) {
        SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date(session.timestamp))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = session.type,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryText,
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        text = dateFormatted,
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SoftBlueBg)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "+${session.coinsEarned} Coins",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue
                    )
                }
            }

            // Session Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${session.steps} steps",
                    fontSize = 13.sp,
                    color = PrimaryText,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${String.format(Locale.getDefault(), "%.2f", session.distanceKm)} km",
                    fontSize = 13.sp,
                    color = PrimaryText,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${session.durationSeconds / 60}m ${session.durationSeconds % 60}s",
                    fontSize = 13.sp,
                    color = PrimaryText,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${session.calories} kcal",
                    fontSize = 13.sp,
                    color = PrimaryText,
                    fontWeight = FontWeight.Medium
                )
            }

            // Route Map Preview if route points exist
            if (routePoints.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    StepPayMapView(
                        modifier = Modifier.fillMaxSize(),
                        userLocation = routePoints.lastOrNull(),
                        routePoints = routePoints,
                        isTracking = false
                    )
                }
            }
        }
    }
}

@Composable
fun WithdrawalHistoryItem(
    withdrawal: WithdrawalEntity,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Payout ${withdrawal.method}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryText,
                        letterSpacing = (-0.3).sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${withdrawal.accountNumber}",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }

                // Status Badge
                val isPaid = withdrawal.status == "Paid" || withdrawal.status == "Berhasil"
                val is7DayVerification = withdrawal.status.contains("7-Day") || withdrawal.status.contains("Pending")
                
                val displayStatus = when {
                    isPaid -> "Paid"
                    is7DayVerification -> withdrawal.status
                    withdrawal.status == "Gagal" -> "Failed"
                    else -> "Processing"
                }

                val (statusBg, statusColor) = when {
                    isPaid -> Pair(SoftGreenBg, SuccessGreen)
                    is7DayVerification -> Pair(com.steppay.app.ui.theme.SoftGoldBg, WarningOrange)
                    else -> Pair(SoftPinkBg, SecondaryText)
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(statusBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = displayStatus,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val displayDollars = if (withdrawal.amountRupiah >= 1000) withdrawal.amountRupiah / 1000 else withdrawal.amountRupiah
                    Text(
                        text = "$$displayDollars.00",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Text(
                        text = "Trx: ${withdrawal.transactionNumber}",
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                }

                Text(
                    text = "- ${String.format(Locale.getDefault(), "%,d", withdrawal.coinCost)} coins",
                    fontSize = 13.sp,
                    color = SecondaryText,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
