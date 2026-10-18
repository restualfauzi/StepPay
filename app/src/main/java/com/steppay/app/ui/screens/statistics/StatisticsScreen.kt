package com.steppay.app.ui.screens.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.steppay.app.R
import com.steppay.app.domain.reward.RewardEngine
import com.steppay.app.ui.StepPayViewModel
import com.steppay.app.ui.components.StepPayTopBar
import com.steppay.app.ui.theme.BackgroundLight
import com.steppay.app.ui.theme.PrimaryBlue
import com.steppay.app.ui.theme.PrimaryText
import com.steppay.app.ui.theme.SecondaryText
import com.steppay.app.ui.theme.SoftBlueBg
import com.steppay.app.ui.theme.SurfaceWhite
import java.util.Calendar
import java.util.Locale

@Composable
fun StatisticsScreen(
    viewModel: StepPayViewModel,
    onBackClick: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val allSessions by viewModel.allSessions.collectAsState()

    val todaySteps = userProfile?.todaySteps ?: 0
    val targetSteps = userProfile?.dailyTargetSteps ?: 5000

    val currentDayName = remember {
        when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Mon"
            Calendar.TUESDAY -> "Tue"
            Calendar.WEDNESDAY -> "Wed"
            Calendar.THURSDAY -> "Thu"
            Calendar.FRIDAY -> "Fri"
            Calendar.SATURDAY -> "Sat"
            Calendar.SUNDAY -> "Sun"
            else -> "Mon"
        }
    }

    // Build real-time weekly activity chart from database
    val weeklyData = remember(todaySteps, allSessions, currentDayName) {
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        days.map { day ->
            if (day == currentDayName) {
                Pair(day, todaySteps)
            } else {
                val dbSteps = allSessions.filter { session ->
                    val cal = Calendar.getInstance().apply { timeInMillis = session.timestamp }
                    val dName = when (cal.get(Calendar.DAY_OF_WEEK)) {
                        Calendar.MONDAY -> "Mon"
                        Calendar.TUESDAY -> "Tue"
                        Calendar.WEDNESDAY -> "Wed"
                        Calendar.THURSDAY -> "Thu"
                        Calendar.FRIDAY -> "Fri"
                        Calendar.SATURDAY -> "Sat"
                        Calendar.SUNDAY -> "Sun"
                        else -> ""
                    }
                    dName == day
                }.sumOf { it.steps }
                Pair(day, dbSteps)
            }
        }
    }

    val totalWeeklySteps = weeklyData.sumOf { it.second }
    val avgSteps = totalWeeklySteps / 7

    Scaffold(
        topBar = {
            StepPayTopBar(
                title = stringResource(R.string.nav_activity),
                onBackClick = onBackClick,
                backgroundColor = SurfaceWhite
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Weekly Bar Chart
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.elevatedCardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Weekly Activity",
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                        color = PrimaryText
                    )
                    Text(
                        text = "Average ${String.format(Locale.getDefault(), "%,d", avgSteps).replace(',', '.')} steps / day",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        color = SecondaryText
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Bar Chart Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val maxVal = (weeklyData.maxOfOrNull { it.second } ?: 5000).toFloat().coerceAtLeast(5000f)
                            val barWidth = 28.dp.toPx()
                            val spacing = (w - (weeklyData.size * barWidth)) / (weeklyData.size + 1)

                            weeklyData.forEachIndexed { idx, pair ->
                                val x = spacing + idx * (barWidth + spacing)
                                val barHeight = ((pair.second / maxVal) * h).coerceAtLeast(10f)
                                val y = h - barHeight

                                drawRoundRect(
                                    color = if (pair.first == currentDayName) PrimaryBlue else SoftBlueBg,
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(12f, 12f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Labels below chart
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        weeklyData.forEach { pair ->
                            Text(
                                text = pair.first,
                                fontSize = 12.sp,
                                fontWeight = if (pair.first == currentDayName) FontWeight.Bold else FontWeight.Medium,
                                color = if (pair.first == currentDayName) PrimaryBlue else SecondaryText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Stat Cards Grid
            Text(
                text = "Today\'s Live Summary",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total Steps",
                    value = String.format(Locale.getDefault(), "%,d", todaySteps).replace(',', '.'),
                    unit = stringResource(R.string.steps_unit),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Est. Distance",
                    value = String.format(Locale.getDefault(), "%.2f", RewardEngine.estimateDistanceKm(todaySteps)),
                    unit = "km",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Est. Calories",
                    value = "${RewardEngine.estimateCalories(todaySteps)}",
                    unit = "kcal",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Goal Progress",
                    value = "${if (targetSteps > 0) ((todaySteps.toFloat() / targetSteps.toFloat()) * 100).toInt().coerceAtMost(100) else 0}%",
                    unit = "completed",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                color = SecondaryText
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                color = PrimaryText
            )
            Text(
                text = unit,
                style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                color = PrimaryBlue
            )
        }
    }
}
