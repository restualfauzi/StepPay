package com.steppay.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.steppay.app.R
import com.steppay.app.ui.theme.BorderLight
import com.steppay.app.ui.theme.PrimaryText
import com.steppay.app.ui.theme.SecondaryText
import com.steppay.app.ui.theme.SoftGreenBg
import com.steppay.app.ui.theme.SuccessGreen
import com.steppay.app.ui.theme.SurfaceWhite
import java.util.Locale

@Composable
fun DailyGoalCard(
    currentSteps: Int,
    targetSteps: Int,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val progress = (currentSteps.toFloat() / targetSteps.toFloat()).coerceIn(0f, 1f)
    val remainingSteps = (targetSteps - currentSteps).coerceAtLeast(0)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(SoftGreenBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsWalk,
                            contentDescription = "Step Icon",
                            tint = SuccessGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.daily_goal),
                            fontSize = 13.sp,
                            color = SecondaryText,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${String.format(Locale.getDefault(), "%,d", targetSteps).replace(',', '.')} ${stringResource(R.string.steps_unit)}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                    }
                }

                if (onDismiss != null) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = SecondaryText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = SuccessGreen,
                trackColor = BorderLight,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${String.format(Locale.getDefault(), "%,d", currentSteps).replace(',', '.')} ${stringResource(R.string.steps_unit)}",
                    fontSize = 12.sp,
                    color = SuccessGreen,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (remainingSteps > 0) stringResource(R.string.steps_remaining, String.format(Locale.getDefault(), "%,d", remainingSteps).replace(',', '.')) else "Goal Achieved! 🎉",
                    fontSize = 12.sp,
                    color = SecondaryText,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}