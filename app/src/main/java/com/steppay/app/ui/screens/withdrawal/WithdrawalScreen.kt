package com.steppay.app.ui.screens.withdrawal

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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.steppay.app.R
import com.steppay.app.ui.StepPayViewModel
import com.steppay.app.ui.components.PrimaryButton
import com.steppay.app.ui.components.StepPayTopBar
import com.steppay.app.ui.theme.BackgroundLight
import com.steppay.app.ui.theme.BorderLight
import com.steppay.app.ui.theme.PrimaryBlue
import com.steppay.app.ui.theme.PrimaryText
import com.steppay.app.ui.theme.SecondaryText
import com.steppay.app.ui.theme.SoftBlueBg
import com.steppay.app.ui.theme.SurfaceWhite
import java.util.Locale

@Composable
fun WithdrawalScreen(
    viewModel: StepPayViewModel,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    initialMethodName: String = "PayPal",
    initialAmount: Int = 5
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val withdrawalMsg by viewModel.withdrawalMessage.collectAsState()

    val validAmount = if (initialAmount in listOf(1, 5, 10, 20)) initialAmount else 1
    var selectedAmount by remember { mutableIntStateOf(validAmount) }
    var accountNum by remember { mutableStateOf(userProfile?.email ?: "user@paypal.com") }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val nominalOptions = listOf(1, 5, 10, 20)

    Scaffold(
        topBar = {
            StepPayTopBar(
                title = stringResource(R.string.payout_title),
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top Payment Card
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.elevatedCardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.paypal),
                            contentDescription = "PayPal Logo",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )

                        Text(
                            text = "PayPal Payout",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )

                        Text(
                            text = "$$selectedAmount.00",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        
                        Text(
                            text = "${String.format(Locale.getDefault(), "%,d", selectedAmount * 10000).replace(',', '.')} coins required",
                            fontSize = 12.sp,
                            color = SecondaryText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Account / Email Input
                OutlinedTextField(
                    value = accountNum,
                    onValueChange = { accountNum = it },
                    label = { Text("PayPal Account Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Select Amount Options Title
                Text(
                    text = "Select Payout Amount",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Nominal Options Grid ($5, $10, $20)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    nominalOptions.forEach { amount ->
                        val coinCost = amount * 100000
                        NominalCard(
                            amount = amount,
                            coinCost = coinCost,
                            isSelected = selectedAmount == amount,
                            onClick = { selectedAmount = amount },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Section: Button & Note
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val totalCost = selectedAmount * 100000
                val totalBalanceCoins = userProfile?.totalCoins ?: 0
                val hasEnoughCoins = totalBalanceCoins >= totalCost
                
                com.steppay.app.ui.components.PrimaryButton(
                    text = "Withdraw $$selectedAmount",
                    enabled = hasEnoughCoins,
                    onClick = { showConfirmDialog = true }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "PayPal processing takes 24-48 business hours",
                    fontSize = 12.sp,
                    color = SecondaryText,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // BEAUTIFUL CONFIRMATION DIALOG
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            containerColor = SurfaceWhite,
            shape = RoundedCornerShape(22.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(SoftBlueBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Confirm PayPal Payout",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Please verify your payout details:",
                        fontSize = 13.sp,
                        color = SecondaryText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftBlueBg),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Method:", fontSize = 13.sp, color = SecondaryText)
                                Text("PayPal", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("PayPal Email:", fontSize = 13.sp, color = SecondaryText)
                                Text(accountNum, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Payout Amount:", fontSize = 13.sp, color = SecondaryText)
                                Text("$$selectedAmount.00", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Coin Cost:", fontSize = 13.sp, color = SecondaryText)
                                Text("${String.format(Locale.getDefault(), "%,d", selectedAmount * 10000).replace(',', '.')} Coins", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                            }
                        }
                    }

                    // Anti-Fraud Notice Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = com.steppay.app.ui.theme.SoftGoldBg),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Security",
                                tint = com.steppay.app.ui.theme.WarningOrange,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🛡️ Anti-Fraud Verification: Log in daily for 7 consecutive days to complete account verification and release cash! Progress: Day 1/7 recorded.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = com.steppay.app.ui.theme.WarningOrange,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.processWithdrawal(
                            method = "PayPal",
                            accountNumber = accountNum,
                            amountRupiah = selectedAmount * 10000,
                            coinCost = selectedAmount * 10000,
                            onSuccess = onSuccess
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Confirm & Submit", color = SurfaceWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showConfirmDialog = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel", color = SecondaryText)
                }
            }
        )
    }

    // BEAUTIFUL RESULT NOTIFICATION DIALOG
    if (withdrawalMsg != null) {
        val isSuccess = withdrawalMsg?.contains("successfully", ignoreCase = true) == true
        AlertDialog(
            onDismissRequest = { viewModel.clearWithdrawalMessage() },
            containerColor = SurfaceWhite,
            shape = RoundedCornerShape(22.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(SoftBlueBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    text = if (isSuccess) "Payout Request Sent! 🎉" else "Payout Notice",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = withdrawalMsg ?: "",
                    fontSize = 14.sp,
                    color = SecondaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearWithdrawalMessage() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("OK", color = SurfaceWhite)
                }
            }
        )
    }
}

@Composable
fun NominalCard(
    amount: Int,
    coinCost: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) PrimaryBlue else BorderLight,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SoftBlueBg else SurfaceWhite
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$$amount.00",
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) PrimaryBlue else PrimaryText
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${String.format(Locale.getDefault(), "%,d", coinCost).replace(',', '.')} coins",
                fontSize = 10.sp,
                color = SecondaryText
            )
        }
    }
}
