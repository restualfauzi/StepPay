package com.steppay.app.ui.screens.profile

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.InsertChartOutlined
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.steppay.app.R
import com.steppay.app.domain.model.SupportedCountry
import com.steppay.app.domain.reward.RewardEngine
import com.steppay.app.ui.StepPayViewModel
import com.steppay.app.ui.components.BottomNavigationBar
import com.steppay.app.ui.components.StepPayTopBar
import com.steppay.app.ui.navigation.Screen
import com.steppay.app.ui.theme.BackgroundLight
import com.steppay.app.ui.theme.BorderLight
import com.steppay.app.ui.theme.PrimaryBlue
import com.steppay.app.ui.theme.PrimaryText
import com.steppay.app.ui.theme.SecondaryText
import com.steppay.app.ui.theme.SoftBlueBg
import com.steppay.app.ui.theme.SurfaceWhite
import java.util.Locale

@Composable
fun ProfileScreen(
    viewModel: StepPayViewModel,
    onNavigate: (Screen) -> Unit
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val countryCode by viewModel.selectedCountry.collectAsState()

    val currentCountry = SupportedCountry.fromCode(countryCode)

    val totalCoins = userProfile?.totalCoins ?: 0
    val todaySteps = userProfile?.todaySteps ?: 0
    val targetSteps = userProfile?.dailyTargetSteps ?: 5000

    var showTargetDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            StepPayTopBar(
                title = stringResource(R.string.nav_profile),
                backgroundColor = SurfaceWhite
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Screen.Profile.route,
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Clean iOS Header User Profile Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar
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
                                .size(72.dp)
                                .clip(CircleShape)
                                .border(2.dp, SoftBlueBg, CircleShape)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_avatar_boy),
                            contentDescription = "User Avatar",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .border(2.dp, SoftBlueBg, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = userProfile?.name ?: "User",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText,
                        letterSpacing = (-0.4).sp
                    )
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onNavigate(Screen.Achievements) },
                            modifier = Modifier.height(36.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text(
                                text = "🏆 Achievements",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SurfaceWhite
                            )
                        }
                        
                        Button(
                            onClick = { onNavigate(Screen.Sneakers) },
                            modifier = Modifier.height(36.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text(
                                text = "👟 Sneakers Lvl ${userProfile?.shoeLevel ?: 1}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SurfaceWhite
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val memberId = userProfile?.memberId ?: "SP12345678"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SoftBlueBg)
                            .clickable {
                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("User ID", memberId)
                                clipboard.setPrimaryClip(clip)
                                android.widget.Toast.makeText(context, "User ID ($memberId) copied!", android.widget.Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "UID: $memberId",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // 3 Simple Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileStatItem(
                            value = String.format(Locale.getDefault(), "%,d", todaySteps).replace(',', '.'),
                            label = stringResource(R.string.steps_unit)
                        )
                        Box(
                            modifier = Modifier
                                .width(0.5.dp)
                                .height(24.dp)
                                .background(BorderLight)
                        )
                        ProfileStatItem(
                            value = String.format(Locale.getDefault(), "%.1f", RewardEngine.estimateDistanceKm(todaySteps)),
                            label = "km"
                        )
                        Box(
                            modifier = Modifier
                                .width(0.5.dp)
                                .height(24.dp)
                                .background(BorderLight)
                        )
                        ProfileStatItem(
                            value = "${RewardEngine.estimateCalories(todaySteps)}",
                            label = "kcal"
                        )
                    }
                }
            }

            // Total Coins Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                    .clickable { onNavigate(Screen.History) },
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.dolar),
                            contentDescription = "Coins",
                            modifier = Modifier.size(38.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "TOTAL BALANCE (100,000 Coins = $1.00)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SecondaryText,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = String.format(Locale.getDefault(), "%,d", totalCoins).replace(',', '.'),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText,
                                letterSpacing = (-0.4).sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = SecondaryText,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            // Invite a Friend / Referral Card
            val referralCode = userProfile?.referralCode ?: "SP-WAIT"
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clickable { onNavigate(Screen.InviteFriends) },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🎁 Invite & Earn 10% Commission",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Text(
                            text = "Both get 10,000 Coins + 10% Lifetime Bonus!",
                            fontSize = 12.sp,
                            color = SecondaryText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFD700).copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Your Code: ",
                                fontSize = 12.sp,
                                color = PrimaryText
                            )
                            Text(
                                text = referralCode,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }
                    }
                    
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Open",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // iOS Inset Grouped Section 1: ACCOUNT & GOALS
            ProfileSectionGroup(
                title = "GOALS & PAYOUTS",
                items = listOf(
                    ProfileGroupItemData(
                        title = stringResource(R.string.daily_goal),
                        subtitle = "${String.format(Locale.getDefault(), "%,d", targetSteps).replace(',', '.')} ${stringResource(R.string.steps_unit)}",
                        icon = Icons.Default.TrackChanges,
                        onClick = { showTargetDialog = true }
                    ),
                    ProfileGroupItemData(
                        title = stringResource(R.string.payout_method),
                        subtitle = userProfile?.paymentMethod ?: "PayPal / Bank Transfer",
                        icon = Icons.Default.CreditCard,
                        onClick = { onNavigate(Screen.Withdrawal) }
                    )
                )
            )

            // iOS Inset Grouped Section 2: ACTIVITY & HISTORY
            ProfileSectionGroup(
                title = "ACTIVITY & HISTORY",
                items = listOf(
                    ProfileGroupItemData(
                        title = stringResource(R.string.nav_activity),
                        subtitle = "Active Workout Tracking & GPS",
                        icon = Icons.AutoMirrored.Filled.DirectionsRun,
                        onClick = { onNavigate(Screen.ActivityTracking) }
                    ),
                    ProfileGroupItemData(
                        title = "Activity Statistics",
                        subtitle = stringResource(R.string.stats_subtitle),
                        icon = Icons.Default.InsertChartOutlined,
                        onClick = { onNavigate(Screen.Statistics) }
                    ),
                    ProfileGroupItemData(
                        title = stringResource(R.string.nav_history),
                        subtitle = "Coins & Payout transactions",
                        icon = Icons.Default.History,
                        onClick = { onNavigate(Screen.History) }
                    )
                )
            )

            // iOS Inset Grouped Section 3: APP SETTINGS
            ProfileSectionGroup(
                title = "HELP & SETTINGS",
                items = listOf(
                    ProfileGroupItemData(
                        title = stringResource(R.string.nav_info),
                        subtitle = stringResource(R.string.support_subtitle),
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        onClick = { onNavigate(Screen.Info) }
                    ),
                    ProfileGroupItemData(
                        title = stringResource(R.string.nav_settings),
                        subtitle = stringResource(R.string.app_preferences),
                        icon = Icons.Default.Settings,
                        onClick = { onNavigate(Screen.Settings) }
                    )
                )
            )
            // iOS Inset Grouped Section 4: LEGAL & PRIVACY
            ProfileSectionGroup(
                title = "LEGAL & PRIVACY",
                items = listOf(
                    ProfileGroupItemData(
                        title = "Privacy Policy",
                        subtitle = "Data safety & privacy protection",
                        icon = Icons.Default.PrivacyTip,
                        onClick = { onNavigate(Screen.PrivacyPolicy) }
                    ),
                    ProfileGroupItemData(
                        title = "Terms of Service",
                        subtitle = "Rules & account terms",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        onClick = { onNavigate(Screen.TermsOfService) }
                    )
                )
            )

            // Logout Section
            ProfileSectionGroup(
                title = "ACCOUNT ACTION",
                items = listOf(
                    ProfileGroupItemData(
                        title = "Sign Out",
                        subtitle = "Log out from your account",
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        onClick = { showLogoutDialog = true },
                        isDestructive = true
                    )
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // LOGOUT CONFIRMATION DIALOG
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = SurfaceWhite,
            title = {
                Text(
                    text = "Sign Out",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to log out? You will need to sign in with Google again to access your account.",
                    fontSize = 14.sp,
                    color = SecondaryText
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.signOut()
                        onNavigate(Screen.Onboarding)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFFE53935))
                ) {
                    Text("Yes, Sign Out", color = SurfaceWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                androidx.compose.material3.OutlinedButton(
                    onClick = { showLogoutDialog = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel", color = SecondaryText)
                }
            }
        )
    }

    // DAILY STEP TARGET DIALOG
    if (showTargetDialog) {
        val targetOptions = listOf(3000, 5000, 8000, 10000, 12000, 15000)
        var selectedTarget by remember { mutableIntStateOf(targetSteps) }

        AlertDialog(
            onDismissRequest = { showTargetDialog = false },
            containerColor = SurfaceWhite,
            title = {
                Text(
                    text = "Daily Step Target",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Choose your daily goal to earn coins faster:",
                        fontSize = 13.sp,
                        color = SecondaryText
                    )
                    targetOptions.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedTarget == option) SoftBlueBg else SurfaceWhite)
                                .border(1.dp, if (selectedTarget == option) PrimaryBlue else BorderLight, RoundedCornerShape(10.dp))
                                .clickable { selectedTarget = option }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${String.format(Locale.getDefault(), "%,d", option).replace(',', '.')} steps",
                                fontSize = 15.sp,
                                fontWeight = if (selectedTarget == option) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTarget == option) PrimaryBlue else PrimaryText
                            )
                            if (selectedTarget == option) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateDailyTarget(selectedTarget)
                        showTargetDialog = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Save Target Goal", color = SurfaceWhite)
                }
            }
        )
    }
}

@Composable
fun ProfileStatItem(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText,
            letterSpacing = (-0.3).sp
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = SecondaryText
        )
    }
}

data class ProfileGroupItemData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val isDestructive: Boolean = false
)

@Composable
fun ProfileSectionGroup(
    title: String,
    items: List<ProfileGroupItemData>
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = SecondaryText,
            letterSpacing = 1.2.sp,
            modifier = Modifier.padding(start = 6.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { item.onClick() }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            val tintColor = if (item.isDestructive) androidx.compose.ui.graphics.Color(0xFFE53935) else PrimaryBlue
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = tintColor,
                                modifier = Modifier.size(22.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = item.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (item.isDestructive) androidx.compose.ui.graphics.Color(0xFFE53935) else PrimaryText,
                                    letterSpacing = (-0.3).sp
                                )
                                Text(
                                    text = item.subtitle,
                                    fontSize = 12.sp,
                                    color = SecondaryText
                                )
                            }
                        }

                        if (!item.isDestructive) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null,
                                tint = SecondaryText,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    if (index < items.lastIndex) {
                        HorizontalDivider(
                            color = BorderLight,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(start = 50.dp)
                        )
                    }
                }
            }
        }
    }
}
