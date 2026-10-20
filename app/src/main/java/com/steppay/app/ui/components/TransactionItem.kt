package com.steppay.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.steppay.app.R
import com.steppay.app.data.local.entity.CoinTransactionEntity
import com.steppay.app.ui.theme.DangerRed
import com.steppay.app.ui.theme.PrimaryBlue
import com.steppay.app.ui.theme.PrimaryText
import com.steppay.app.ui.theme.SecondaryText
import com.steppay.app.ui.theme.SoftBlueBg
import com.steppay.app.ui.theme.SoftGoldBg
import com.steppay.app.ui.theme.SoftGreenBg
import com.steppay.app.ui.theme.SoftPinkBg
import com.steppay.app.ui.theme.SuccessGreen
import java.util.Locale

@Composable
fun TransactionItem(
    transaction: CoinTransactionEntity,
    modifier: Modifier = Modifier
) {
    val title = transaction.title

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Render specific matching Icon / Image directly without background container
            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    title.contains("Sneakers", true) || title.contains("Shoe", true) -> {
                        Image(
                            painter = painterResource(id = R.drawable.steppay_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                    }
                    title.contains("Payout", true) || title.contains("PayPal", true) || title.contains("Withdraw", true) -> {
                        Image(
                            painter = painterResource(id = R.drawable.paypal),
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                    }
                    title.contains("Achievement", true) || title.contains("Badge", true) -> {
                        Image(
                            painter = painterResource(id = R.drawable.trophy),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    title.contains("Spin", true) || title.contains("Lucky", true) -> {
                        Image(
                            painter = painterResource(id = R.drawable.spinn),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    title.contains("Referral", true) || title.contains("Friend", true) || title.contains("Invite", true) -> {
                        Image(
                            painter = painterResource(id = R.drawable.invite),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    title.contains("Water", true) || title.contains("Hydration", true) -> {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = Color(0xFF03A9F4),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    title.contains("Walk", true) || title.contains("Jalan", true) || title.contains("Steps", true) -> {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    title.contains("Run", true) || title.contains("Lari", true) -> {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    else -> {
                        Image(
                            painter = painterResource(id = R.drawable.gift),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = transaction.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    text = transaction.subtitle,
                    fontSize = 12.sp,
                    color = SecondaryText,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        val amountText = if (transaction.isPositive) {
            "+${String.format(Locale.getDefault(), "%,d", transaction.amountCoins).replace(',', '.')} coins"
        } else {
            "-${String.format(Locale.getDefault(), "%,d", transaction.amountCoins).replace(',', '.')} coins"
        }

        Text(
            text = amountText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (transaction.isPositive) SuccessGreen else DangerRed,
            letterSpacing = (-0.2).sp
        )
    }
}
