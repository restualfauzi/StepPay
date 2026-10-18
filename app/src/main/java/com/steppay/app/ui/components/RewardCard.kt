package com.steppay.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.steppay.app.R
import com.steppay.app.ui.theme.BorderLight
import com.steppay.app.ui.theme.PrimaryBlue
import com.steppay.app.ui.theme.PrimaryText
import com.steppay.app.ui.theme.SoftGoldBg
import com.steppay.app.ui.theme.SurfaceWhite
import com.steppay.app.ui.theme.WarningOrange

data class RewardItem(
    val id: String,
    val title: String,
    val coinCost: Int,
    val category: String,
    val iconUrl: String = "",
    val iconResId: Int = R.drawable.wallet,
    val badgeText: String = ""
)

@Composable
fun RewardCard(
    reward: RewardItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (reward.badgeText.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SoftGoldBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = reward.badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarningOrange
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(52.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (reward.iconUrl.isNotBlank()) {
                        AsyncImage(
                            model = reward.iconUrl,
                            contentDescription = reward.title,
                            placeholder = painterResource(id = reward.iconResId),
                            error = painterResource(id = reward.iconResId),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(48.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = reward.iconResId),
                            contentDescription = reward.title,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = reward.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    letterSpacing = (-0.3).sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${String.format(java.util.Locale.getDefault(), "%,d", reward.coinCost).replace(',', '.')} coins",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryBlue,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
