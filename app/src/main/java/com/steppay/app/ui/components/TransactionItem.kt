package com.steppay.app.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun TransactionItem(
    transaction: CoinTransactionEntity,
    modifier: Modifier = Modifier
) {
    val (icon, tintColor) = when {
        transaction.title.contains("Walk", ignoreCase = true) || transaction.title.contains("Jalan", ignoreCase = true) ->
            Pair(Icons.Default.DirectionsWalk, SuccessGreen)
        transaction.title.contains("Run", ignoreCase = true) || transaction.title.contains("Lari", ignoreCase = true) ->
            Pair(Icons.Default.DirectionsRun, PrimaryBlue)
        transaction.title.contains("Bonus", ignoreCase = true) ->
            Pair(Icons.Default.CardGiftcard, SuccessGreen)
        else ->
            Pair(Icons.Default.AccountBalanceWallet, DangerRed)
    }

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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tintColor,
                modifier = Modifier.size(28.dp)
            )

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
            "+${String.format("%,d", transaction.amountCoins).replace(',', '.')} coins"
        } else {
            "-${String.format("%,d", transaction.amountCoins).replace(',', '.')} coins"
        }

        Text(
            text = amountText,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (transaction.isPositive) SuccessGreen else DangerRed,
            letterSpacing = (-0.2).sp
        )
    }
}
