package com.steppay.app.ui.screens.bonus

import android.app.Activity
import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.steppay.app.ads.AdBanner
import com.steppay.app.ads.AdManager
import com.steppay.app.data.config.OfferwallConfig
import com.steppay.app.domain.reward.RewardEngine
import com.steppay.app.ui.StepPayViewModel
import com.steppay.app.ui.components.BottomNavigationBar
import com.steppay.app.ui.components.CoinBalanceCard
import com.steppay.app.ui.components.OfferwallWebViewSheet
import com.steppay.app.ui.components.StepPayTopBar
import com.steppay.app.ui.navigation.Screen
import com.steppay.app.ui.theme.BackgroundLight
import com.steppay.app.ui.theme.BorderLight
import com.steppay.app.ui.theme.PrimaryBlue
import com.steppay.app.ui.theme.PrimaryText
import com.steppay.app.ui.theme.SecondaryText
import com.steppay.app.ui.theme.SoftBlueBg
import com.steppay.app.ui.theme.SoftGoldBg
import com.steppay.app.ui.theme.SuccessGreen
import com.steppay.app.ui.theme.SurfaceWhite
import com.steppay.app.ui.theme.WarningOrange

data class StepMilestone(
    val level: Int,
    val targetSteps: Int,
    val baseCoins: Int,
    val doubleCoins: Int,
    val title: String
)

val stepMilestones = listOf(
    StepMilestone(1, 50, 50, 100, "50 Steps Goal"),
    StepMilestone(2, 200, 100, 200, "200 Steps Goal"),
    StepMilestone(3, 500, 150, 300, "500 Steps Goal"),
    StepMilestone(4, 1000, 250, 500, "1,000 Steps Goal"),
    StepMilestone(5, 2500, 500, 1000, "2,500 Steps Goal"),
    StepMilestone(6, 5000, 1000, 2000, "5,000 Steps Goal"),
    StepMilestone(7, 10000, 2000, 4000, "10,000 Steps Goal")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BonusScreen(
    viewModel: StepPayViewModel,
    onNavigate: (Screen) -> Unit
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val totalCoins = userProfile?.totalCoins ?: 0
    val rupiahValue = RewardEngine.calculateRupiahValue(totalCoins)
    val todaySteps = userProfile?.todaySteps ?: 0

    var localClaimedTasks by remember { mutableStateOf(setOf<String>()) }

    fun isToday(timestamp: Long): Boolean {
        val fmt = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return fmt.format(java.util.Date(timestamp)) == fmt.format(java.util.Date())
    }

    fun isTaskClaimed(taskName: String): Boolean {
        return localClaimedTasks.contains(taskName) ||
                allTransactions.any { isToday(it.timestamp) && it.title.contains(taskName, ignoreCase = true) }
    }

    fun triggerMissionWithAd(
        is2xReward: Boolean = false,
        rewardAmount: Int,
        missionTitle: String,
        missionSubtitle: String,
        onSuccess: () -> Unit
    ) {
        val activity = context as? Activity
        if (activity != null) {
            if (is2xReward) {
                AdManager.showRewardedAd(
                    activity = activity,
                    onRewardEarned = {
                        viewModel.claimBonusCoins(rewardAmount, missionTitle, missionSubtitle)
                        onSuccess()
                    },
                    onFailed = {
                        viewModel.claimBonusCoins(rewardAmount, missionTitle, missionSubtitle)
                        onSuccess()
                    }
                )
            } else {
                AdManager.showRewardedAd(
                    activity = activity,
                    onRewardEarned = {
                        viewModel.claimBonusCoins(rewardAmount, missionTitle, missionSubtitle)
                        onSuccess()
                    },
                    onFailed = {
                        AdManager.showInterstitialAd(activity)
                        viewModel.claimBonusCoins(rewardAmount, missionTitle, missionSubtitle)
                        onSuccess()
                    }
                )
            }
        } else {
            viewModel.claimBonusCoins(rewardAmount, missionTitle, missionSubtitle)
            onSuccess()
        }
    }

    var showTapjoySheet by remember { mutableStateOf(false) }
    var showBitLabsSheet by remember { mutableStateOf(false) }
    var showClaimDialog by remember { mutableStateOf<String?>(null) }
    var claimedCoinsAmount by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            StepPayTopBar(
                title = stringResource(R.string.nav_bonus),
                backgroundColor = SurfaceWhite
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Screen.Bonus.route,
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Coin Balance Card
            CoinBalanceCard(
                totalCoins = totalCoins,
                rupiahValue = rupiahValue,
                onHistoryClick = { onNavigate(Screen.Rewards) }
            )

            // TOP HIGHLIGHTS (WHEEL & REFERRAL)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Spin Wheel Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(20.dp))
                        .clickable {
                            onNavigate(Screen.LuckySpin)
                        },
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.spinn),
                            contentDescription = "Spin Wheel",
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Lucky Spin",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Text(
                            text = "Win up to 200 Coins",
                            fontSize = 11.sp,
                            color = PrimaryBlue
                        )
                    }
                }

                // Referral Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(20.dp))
                        .clickable {
                            onNavigate(Screen.InviteFriends)
                        },
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.invite),
                            contentDescription = "Invite Friends",
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Invite Friends",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Text(
                            text = "+10000/Friend",
                            fontSize = 11.sp,
                            color = PrimaryBlue
                        )
                    }
                }
            }

            // Header Title
            Column {
                Text(
                    text = "Coin Bonus Offers",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    letterSpacing = (-0.4).sp
                )
                Text(
                    text = "Play games or complete surveys to earn coins!",
                    fontSize = 13.sp,
                    color = SecondaryText
                )
            }

            // 1. TAPJOY GAMES CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(22.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.tapjoy),
                                contentDescription = "Tapjoy Games",
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Tapjoy Games",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryText
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(SoftBlueBg)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "POPULAR",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryBlue
                                        )
                                    }
                                }
                                Text(
                                    text = "Play Games & Earn Coins",
                                    fontSize = 13.sp,
                                    color = SecondaryText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Play exciting featured games from Tapjoy and earn up to 50,000 Coins per game!",
                        fontSize = 13.sp,
                        color = PrimaryText,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(SoftBlueBg)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Up to +99,999 Coins",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }

                        Button(
                            onClick = { showTapjoySheet = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text(
                                text = "Play Games",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SurfaceWhite
                            )
                        }
                    }
                }
            }

            // 2. BITLABS SURVEYS CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(22.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.survey),
                                contentDescription = "BitLabs Surveys",
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = "BitLabs Surveys",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryText
                                )
                                Text(
                                    text = "Take Surveys & Get Paid",
                                    fontSize = 13.sp,
                                    color = SecondaryText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Share your opinion in quick market research surveys and earn high coin rewards!",
                        fontSize = 13.sp,
                        color = PrimaryText,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(SoftBlueBg)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "High Payouts",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }

                        Button(
                            onClick = { showBitLabsSheet = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text(
                                text = "Take Surveys",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SurfaceWhite
                            )
                        }
                    }
                }
            }

            // EXTRA DAILY BONUS TASKS
            Text(
                text = "Daily Bonus Tasks",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue,
                modifier = Modifier.padding(top = 8.dp)
            )

            // 0. Progressive Step Milestone Mission Card (50 -> 200 -> 500 -> 1k -> 2.5k -> 5k -> 10k steps!)
            val activeMilestone = stepMilestones.firstOrNull { milestone ->
                !isTaskClaimed(milestone.title)
            }
            val isAllMilestonesCompleted = activeMilestone == null
            val currentMilestoneTarget = activeMilestone?.targetSteps ?: 10000
            val isStepGoalReached = todaySteps >= currentMilestoneTarget && !isAllMilestonesCompleted
            val milestoneTitle = activeMilestone?.title ?: "All Step Goals Completed!"

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SoftBlueBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = milestoneTitle,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryText
                                )
                                Text(
                                    text = if (isAllMilestonesCompleted) "10,000 / 10,000 steps completed" else "Progress: $todaySteps / $currentMilestoneTarget steps",
                                    fontSize = 12.sp,
                                    color = if (isStepGoalReached) SuccessGreen else SecondaryText,
                                    fontWeight = if (isStepGoalReached) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    // Action Buttons Row: Normal (+baseCoins) and 2x Rewards (+doubleCoins via Video Ad)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val milestone = activeMilestone
                        val baseAmount = milestone?.baseCoins ?: 0
                        val doubleAmount = milestone?.doubleCoins ?: 0

                        // Option 1: Standard Claim +baseAmount Coins (With mandatory ad)
                        Button(
                            onClick = {
                                if (!isStepGoalReached || milestone == null) return@Button
                                localClaimedTasks = localClaimedTasks + milestone.title
                                triggerMissionWithAd(
                                    is2xReward = false,
                                    rewardAmount = baseAmount,
                                    missionTitle = milestone.title,
                                    missionSubtitle = "Step Goal Reached (+$baseAmount)"
                                ) {
                                    Toast.makeText(context, "${milestone.title} Completed! +$baseAmount Coins added.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = isStepGoalReached,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                                disabledContainerColor = BorderLight
                            )
                        ) {
                            Text(
                                text = if (isAllMilestonesCompleted) "Completed ✓" else if (!isStepGoalReached) "Locked" else "+$baseAmount Coins",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isStepGoalReached) SurfaceWhite else SecondaryText
                            )
                        }

                        // Option 2: 2x Double Reward +doubleAmount Coins (Via Rewarded Video Ad)
                        Button(
                            onClick = {
                                if (!isStepGoalReached || milestone == null) return@Button
                                localClaimedTasks = localClaimedTasks + milestone.title
                                triggerMissionWithAd(
                                    is2xReward = true,
                                    rewardAmount = doubleAmount,
                                    missionTitle = milestone.title,
                                    missionSubtitle = "2x Double Step Goal (+$doubleAmount)"
                                ) {
                                    Toast.makeText(context, "2x Double Reward! +$doubleAmount Coins added!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = isStepGoalReached,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WarningOrange,
                                disabledContainerColor = BorderLight
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircleFilled,
                                    contentDescription = "2x Video",
                                    tint = if (isStepGoalReached) SurfaceWhite else SecondaryText,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isAllMilestonesCompleted) "Completed ✓" else if (!isStepGoalReached) "Locked" else "2x (+$doubleAmount)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isStepGoalReached) SurfaceWhite else SecondaryText
                                )
                            }
                        }
                    }
                }
            }

            // 1. Rate 5 Stars Card
            val isRateClaimed = isTaskClaimed("Rate 5 Stars")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SoftGoldBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = WarningOrange,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Rate 5 Stars on Play Store",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isRateClaimed) "Review Submitted" else "Rate StepPay 5 stars & leave a review",
                                fontSize = 12.sp,
                                color = SecondaryText
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (isRateClaimed) return@Button
                            localClaimedTasks = localClaimedTasks + "Rate 5 Stars"
                            val packageName = context.packageName
                            try {
                                val intent = android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    android.net.Uri.parse("market://details?id=$packageName")
                                ).apply {
                                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                val intent = android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    android.net.Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                                ).apply {
                                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            }

                            triggerMissionWithAd(
                                is2xReward = false,
                                rewardAmount = 500,
                                missionTitle = "Rate 5 Stars",
                                missionSubtitle = "Play Store Review (+500)"
                            ) {
                                Toast.makeText(context, "Thank you for rating StepPay 5 stars! +500 Coins earned!", Toast.LENGTH_LONG).show()
                            }
                        },
                        enabled = !isRateClaimed,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WarningOrange,
                            disabledContainerColor = BorderLight
                        )
                    ) {
                        Text(
                            text = if (isRateClaimed) "Claimed ✓" else "+500",
                            color = if (isRateClaimed) SecondaryText else SurfaceWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 2. Watch Rewarded Ads Card (Up to 10 Ads per day)
            val watchAdCountToday = allTransactions.count {
                isToday(it.timestamp) && (it.title.contains("Watch Video Ad", ignoreCase = true) || it.title.contains("Rewarded Ad", ignoreCase = true))
            }
            val isMaxAdCountReached = watchAdCountToday >= 10

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SoftBlueBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircleFilled,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Watch Rewarded Ads",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryText
                            )
                            Text(
                                text = if (isMaxAdCountReached) "Daily Limit Reached (10/10)" else "Earn +200 Coins per Ad ($watchAdCountToday/10 Today)",
                                fontSize = 12.sp,
                                color = SecondaryText
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (isMaxAdCountReached) return@Button
                            val nextAdIndex = watchAdCountToday + 1
                            triggerMissionWithAd(
                                is2xReward = true,
                                rewardAmount = 200,
                                missionTitle = "Watch Video Ad #$nextAdIndex",
                                missionSubtitle = "Rewarded Ad (+200)"
                            ) {
                                Toast.makeText(context, "Earned +200 Coins! ($nextAdIndex/10)", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !isMaxAdCountReached,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            disabledContainerColor = BorderLight
                        )
                    ) {
                        Text(
                            text = if (isMaxAdCountReached) "Done ✓" else "$watchAdCountToday/10",
                            color = if (isMaxAdCountReached) SecondaryText else SurfaceWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 3. Daily Check-in Card
            val isCheckInClaimed = isTaskClaimed("Daily Check-in")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SoftBlueBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Daily Check-in Streak",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryText
                            )
                            Text(
                                text = if (isCheckInClaimed) "Already Checked-in Today" else "Come back daily for bigger rewards",
                                fontSize = 12.sp,
                                color = SecondaryText
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (isCheckInClaimed) return@Button
                            localClaimedTasks = localClaimedTasks + "Daily Check-in"
                            triggerMissionWithAd(
                                is2xReward = false,
                                rewardAmount = 5,
                                missionTitle = "Daily Check-in",
                                missionSubtitle = "Day 1 Bonus (+5)"
                            ) {
                                Toast.makeText(context, "Day 1 Check-in! +5 Coins added.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !isCheckInClaimed,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            disabledContainerColor = BorderLight
                        )
                    ) {
                        Text(
                            text = if (isCheckInClaimed) "Claimed ✓" else "+5",
                            color = if (isCheckInClaimed) SecondaryText else SurfaceWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 4. Early Bird Bonus (Morning Walk - Requires 1,000 steps)
            val isMorningWalkClaimed = isTaskClaimed("Morning Walk")
            val isMorningWalkReached = todaySteps >= 1000
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SoftBlueBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Morning Walk",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryText
                            )
                            Text(
                                text = if (isMorningWalkClaimed) "Already Completed Today" else "Progress: $todaySteps / 1,000 steps",
                                fontSize = 12.sp,
                                color = if (isMorningWalkReached) SuccessGreen else SecondaryText
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (!isMorningWalkReached || isMorningWalkClaimed) return@Button
                            localClaimedTasks = localClaimedTasks + "Morning Walk"
                            triggerMissionWithAd(
                                is2xReward = false,
                                rewardAmount = 200,
                                missionTitle = "Morning Walk",
                                missionSubtitle = "Morning Walk Goal (+200)"
                            ) {
                                Toast.makeText(context, "Morning Walk Goal Completed! +200 Coins added.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = isMorningWalkReached && !isMorningWalkClaimed,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            disabledContainerColor = BorderLight
                        )
                    ) {
                        Text(
                            text = if (isMorningWalkClaimed) "Claimed ✓" else if (!isMorningWalkReached) "Locked" else "+200",
                            color = if (isMorningWalkReached && !isMorningWalkClaimed) SurfaceWhite else SecondaryText,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 5. Hydration Tracker
            val isHydrationClaimed = isTaskClaimed("Hydration Task")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SoftBlueBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Stay Hydrated",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryText
                            )
                            Text(
                                text = if (isHydrationClaimed) "Water Logged Today" else "Log 8 glasses of water today",
                                fontSize = 12.sp,
                                color = SecondaryText
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (isHydrationClaimed) return@Button
                            localClaimedTasks = localClaimedTasks + "Hydration Task"
                            triggerMissionWithAd(
                                is2xReward = false,
                                rewardAmount = 20,
                                missionTitle = "Hydration Task",
                                missionSubtitle = "Log Water (+20)"
                            ) {
                                Toast.makeText(context, "Water logged! +20 Coins.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !isHydrationClaimed,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            disabledContainerColor = BorderLight
                        )
                    ) {
                        Text(
                            text = if (isHydrationClaimed) "Claimed ✓" else "+20",
                            color = if (isHydrationClaimed) SecondaryText else SurfaceWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 6. Share With Friends (Real System Intent Chooser to WhatsApp / Instagram / Socials)
            val isShareClaimed = isTaskClaimed("Share Stats")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SoftBlueBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Share Your Stats",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                            Text(
                                text = if (isShareClaimed) "Shared Today" else "Share to Instagram / WhatsApp",
                                fontSize = 12.sp,
                                color = SecondaryText
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (isShareClaimed) return@Button
                            localClaimedTasks = localClaimedTasks + "Share Stats"

                            // Launch Android System Share Intent Chooser
                            try {
                                val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        android.content.Intent.EXTRA_TEXT,
                                        "🏃 I walked $todaySteps steps on StepPay today and earned real cash rewards! Join me and start earning cash while walking: https://play.google.com/store/apps/details?id=${context.packageName}"
                                    )
                                }
                                context.startActivity(android.content.Intent.createChooser(shareIntent, "Share StepPay Stats via"))
                            } catch (_: Exception) {}

                            triggerMissionWithAd(
                                is2xReward = false,
                                rewardAmount = 150,
                                missionTitle = "Share Stats",
                                missionSubtitle = "Share to Social Media (+150)"
                            ) {
                                Toast.makeText(context, "Thanks for sharing! +150 Coins added.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !isShareClaimed,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            disabledContainerColor = BorderLight
                        )
                    ) {
                        Text(
                            text = if (isShareClaimed) "Claimed ✓" else "+150",
                            color = if (isShareClaimed) SecondaryText else SurfaceWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // AdMob Banner Ad
            AdBanner(modifier = Modifier.padding(top = 8.dp))

            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    // MODAL BOTTOM SHEET FOR TAPJOY GAMES OFFERWALL
    if (showTapjoySheet) {
        OfferwallWebViewSheet(
            title = "Tapjoy Games Offerwall",
            url = OfferwallConfig.getTapjoyUrl(userProfile?.memberId ?: "sp101"),
            onDismiss = { showTapjoySheet = false }
        )
    }

    // MODAL BOTTOM SHEET FOR BITLABS SURVEYS OFFERWALL
    if (showBitLabsSheet) {
        OfferwallWebViewSheet(
            title = "BitLabs Surveys",
            url = OfferwallConfig.getBitLabsUrl(userProfile?.memberId ?: "sp101"),
            onDismiss = { showBitLabsSheet = false }
        )
    }

    // CLAIM SUCCESS DIALOG
    if (showClaimDialog != null) {
        AlertDialog(
            onDismissRequest = { showClaimDialog = null },
            containerColor = SurfaceWhite,
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Congratulations! Coins Claimed 🎉",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "You successfully completed \"${showClaimDialog}\" and earned +${claimedCoinsAmount} Coins!",
                    fontSize = 14.sp,
                    color = SecondaryText,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.claimBonusCoins(
                            claimedCoinsAmount,
                            showClaimDialog ?: "Bonus Task",
                            "Task Completed (+${claimedCoinsAmount})"
                        )
                        showClaimDialog = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Claim Coins", color = SurfaceWhite)
                }
            }
        )
    }
}
