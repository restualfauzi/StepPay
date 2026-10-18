package com.steppay.app.ui.screens.leaderboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.steppay.app.ui.StepPayViewModel
import com.steppay.app.ui.components.StepPayTopBar
import com.steppay.app.ui.theme.BackgroundLight
import com.steppay.app.ui.theme.DarkNavy
import com.steppay.app.ui.theme.PrimaryBlue
import com.steppay.app.ui.theme.PrimaryText
import com.steppay.app.ui.theme.SecondaryText
import com.steppay.app.ui.theme.SoftBlueBg
import com.steppay.app.ui.theme.SurfaceWhite
import java.util.Locale

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val steps: Int,
    val isCurrentUser: Boolean = false,
    val photoUrl: String? = null
)

@Composable
fun LeaderboardScreen(
    viewModel: StepPayViewModel,
    onBackClick: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val allSessions by viewModel.allSessions.collectAsState()

    val currentSteps = userProfile?.todaySteps ?: 0
    val currentUserName = userProfile?.name ?: "You"
    val currentUserPhoto = userProfile?.photoUrl

    // Build real-time leaderboard list from live database & active step tracking
    val leaderboardData = remember(currentSteps, currentUserName, currentUserPhoto, allSessions) {
        val userList = mutableListOf(
            LeaderboardUser(
                rank = 1,
                name = currentUserName,
                steps = currentSteps,
                isCurrentUser = true,
                photoUrl = currentUserPhoto
            )
        )

        // Add real recorded workout sessions from database
        allSessions.groupBy { it.id }.forEach { (sessionId, sessions) ->
            val sessionSteps = sessions.sumOf { it.steps }
            if (sessionSteps > 0 && sessionSteps != currentSteps) {
                userList.add(
                    LeaderboardUser(
                        rank = 0,
                        name = "Runner #$sessionId",
                        steps = sessionSteps,
                        isCurrentUser = false
                    )
                )
            }
        }

        // Sort by steps descending and assign real-time ranks
        userList.sortedByDescending { it.steps }.mapIndexed { index, user ->
            user.copy(rank = index + 1)
        }
    }

    Scaffold(
        topBar = {
            StepPayTopBar(
                title = stringResource(R.string.nav_leaderboard),
                onBackClick = onBackClick,
                backgroundColor = SurfaceWhite
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Podium Header
            item {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp)),
                    colors = CardDefaults.elevatedCardColors(containerColor = DarkNavy),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🏆 Today\'s Live Leaderboard",
                            style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                            color = SurfaceWhite
                        )
                        Text(
                            text = "Walk more steps to reach the top!",
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                            color = SecondaryText
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Top 3 Podium
                        if (leaderboardData.size >= 3) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                PodiumUser(user = leaderboardData[1], crownColor = Color(0xFFC0C0C0), size = 52.dp)
                                PodiumUser(user = leaderboardData[0], crownColor = Color(0xFFFFD700), size = 68.dp)
                                PodiumUser(user = leaderboardData[2], crownColor = Color(0xFFCD7F32), size = 52.dp)
                            }
                        } else if (leaderboardData.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                PodiumUser(user = leaderboardData[0], crownColor = Color(0xFFFFD700), size = 68.dp)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Live Active Participants",
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    color = PrimaryText,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            // Ranked Users List
            itemsIndexed(leaderboardData) { index, user ->
                LeaderboardRow(user = user)
            }
        }
    }
}

@Composable
fun PodiumUser(
    user: LeaderboardUser,
    crownColor: Color,
    size: androidx.compose.ui.unit.Dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.TopCenter
        ) {
            if (!user.photoUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.photoUrl)
                        .crossfade(true)
                        .error(R.drawable.ic_avatar_boy)
                        .placeholder(R.drawable.ic_avatar_boy)
                        .build(),
                    contentDescription = user.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_avatar_boy),
                    contentDescription = user.name,
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape)
                )
            }
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(crownColor)
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${user.rank}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = user.name.split(" ").first(),
            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = SurfaceWhite
        )
        Text(
            text = "${String.format(Locale.getDefault(), "%,d", user.steps).replace(',', '.')} steps",
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            color = SecondaryText
        )
    }
}

@Composable
fun LeaderboardRow(
    user: LeaderboardUser
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (user.isCurrentUser) SoftBlueBg else SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "#${user.rank}",
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    color = if (user.isCurrentUser) PrimaryBlue else SecondaryText,
                    modifier = Modifier.width(36.dp)
                )

                if (!user.photoUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user.photoUrl)
                            .crossfade(true)
                            .error(R.drawable.ic_avatar_boy)
                            .placeholder(R.drawable.ic_avatar_boy)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_avatar_boy),
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (user.isCurrentUser) "${user.name} (You)" else user.name,
                        style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                        color = PrimaryText
                    )
                    Text(
                        text = "Active today",
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = SecondaryText
                    )
                }
            }

            Text(
                text = "${String.format(Locale.getDefault(), "%,d", user.steps).replace(',', '.')} steps",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        }
    }
}
