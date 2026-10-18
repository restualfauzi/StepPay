package com.steppay.app.ui.screens.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.steppay.app.R
import com.steppay.app.ui.components.StepPayTopBar
import com.steppay.app.ui.theme.BackgroundLight
import com.steppay.app.ui.theme.PrimaryBlue
import com.steppay.app.ui.theme.PrimaryText
import com.steppay.app.ui.theme.SecondaryText
import com.steppay.app.ui.theme.SurfaceWhite

@Composable
fun InfoScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            StepPayTopBar(
                title = "About & Help Center",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // App Branding Header
            Image(
                painter = painterResource(id = R.drawable.steppay_icon),
                contentDescription = "StepPay Logo",
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(22.dp))
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "StepPay v0.0.1 (Early Access)",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )

            Text(
                text = "Walk, Run & Earn Real Rewards",
                fontSize = 13.sp,
                color = PrimaryBlue,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Section 1: How StepPay Works
            HelpGuideCard(
                title = "🏃 How Does StepPay Work?",
                content = "StepPay uses your smartphone's built-in hardware pedometer sensors and GPS location to accurately record your daily walking and running activity in real time.\n\nEvery step you take brings you closer to earning virtual coins, which can be redeemed for real PayPal cash rewards!"
            )

            // Section 2: Progressive Step Milestones
            HelpGuideCard(
                title = "⚡ Progressive Step Goals & Exchange Rate",
                content = "• Official Exchange Rate: $1.00 USD = 10,000 Coins\n• Progressive Step Goals: Daily step rewards scale progressively across 7 levels starting from 50 steps up to 10,000 steps (50 -> 200 -> 500 -> 1,000 -> 2,500 -> 5,000 -> 10,000 steps).\n• 2x Double Rewards: Each step goal offers an optional 2x double coin boost via short rewarded video ads!\n• Active Tracking: Start a workout session in the Activity tab for GPS route mapping and real-time exercise stats."
            )

            // Section 3: Earning Extra Coins (Bonus Center)
            HelpGuideCard(
                title = "🎮 Bonus Center & Task Offers",
                content = "Want to earn coins faster? Visit the Bonus tab to complete additional offers:\n• Rate 5 Stars on Play Store: Rate StepPay 5 stars to claim +500 Coins ($0.05 USD).\n• Tapjoy Games: Play top mobile games and earn up to 50,000 Coins ($5.00 USD).\n• BitLabs Surveys: Answer quick surveys matched to your profile.\n• Daily Tasks: Watch short video ads, check in daily, and spin the wheel for free bonus coins!"
            )

            // Section 4: PayPal Payout Tiers & Anti-Fraud Mission
            HelpGuideCard(
                title = "💳 PayPal Cash Out & 7-Day Verification",
                content = "You can request cash payouts directly to your registered PayPal account from the Rewards or Request Payout screen:\n• $1.00 PayPal Cash = 10,000 Coins\n• $5.00 PayPal Cash = 50,000 Coins\n• $10.00 PayPal Cash = 100,000 Coins\n• $20.00 PayPal Cash = 200,000 Coins\n\n🛡️ Anti-Fraud Requirement: To prevent fake/bot accounts, all payout requests require a 7-Day Daily Login Verification before cash is transferred to your PayPal."
            )

            // Section 5: Anti-Cheat & Fair Play Policy
            HelpGuideCard(
                title = "🛡️ Anti-Cheat & Fair Play Rules",
                content = "To keep StepPay fair for all users:\n• Automated phone shakers, fake GPS apps, VPNs/proxies, emulators, or step simulators are strictly forbidden.\n• Each physical user is limited to one account per device.\n• Accounts violating fair play rules will be permanently suspended and forfeit all coins."
            )

            // Section 6: Frequently Asked Questions (FAQ)
            HelpGuideCard(
                title = "❓ Frequently Asked Questions (FAQ)",
                content = "Q: Why aren't my steps counting?\nA: Ensure Physical Activity and Location permissions are enabled in your device settings, and turn off Battery Saver for StepPay.\n\nQ: How do progressive step goals work?\nA: Start by claiming your first 50 steps goal. Once claimed, the target automatically advances to 200 steps, then 500, 1k, 2.5k, 5k, and 10k steps!\n\nQ: What is the 7-day login requirement?\nA: After requesting a payout, simply open and log into StepPay daily for 7 days to verify your account. Your payout will be released automatically upon verification.\n\nQ: Is StepPay completely free?\nA: Yes! StepPay is 100% free to use. You never need to pay anything to earn rewards."
            )

            // Section 7: Customer Support Contact
            HelpGuideCard(
                title = "📬 Need More Help? Contact Support",
                content = "Developer: Circle Mesh Limited\nIf you have any questions, payout inquiries, or technical issues, our dedicated support team is here to help!\n\n• Email Support: Support@circlemesh.site\n• Response Time: Within 24 business hours\n• Operating Hours: Monday – Friday, 09:00 – 18:00 UTC"
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HelpGuideCard(
    title: String,
    content: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = content,
                fontSize = 13.sp,
                color = SecondaryText,
                lineHeight = 20.sp
            )
        }
    }
}
