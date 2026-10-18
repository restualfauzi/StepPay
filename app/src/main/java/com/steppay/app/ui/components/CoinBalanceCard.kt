package com.steppay.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.steppay.app.R
import com.steppay.app.ui.theme.BorderLight
import com.steppay.app.ui.theme.PrimaryBlue
import com.steppay.app.ui.theme.PrimaryText
import com.steppay.app.ui.theme.SecondaryText
import com.steppay.app.ui.theme.SoftBlueBg
import com.steppay.app.ui.theme.SurfaceWhite
import java.util.Locale

@Composable
fun CoinBalanceCard(
    totalCoins: Int,
    rupiahValue: Int,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.total_coins).uppercase(),
                    fontSize = 11.sp,
                    color = SecondaryText,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )

                // iOS History Action Pill Button
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SoftBlueBg)
                        .clickable { onHistoryClick() }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.nav_history),
                        fontSize = 12.sp,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.2).sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.dolar),
                    contentDescription = "Coin Icon",
                    modifier = Modifier.size(38.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = String.format(Locale.getDefault(), "%,d", totalCoins).replace(',', '.'),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    letterSpacing = (-0.5).sp
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.approx_value, "USD ${(totalCoins / 10000.0).let { String.format(Locale.getDefault(), "%.2f", it) }}"),
                    fontSize = 13.sp,
                    color = SecondaryText,
                    fontWeight = FontWeight.Medium
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(com.steppay.app.ui.theme.SoftGoldBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$1 = 10.000 Coins",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = com.steppay.app.ui.theme.WarningOrange
                    )
                }
            }
        }
    }
}
