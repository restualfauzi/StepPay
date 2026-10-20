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
                text = "StepPay v0.0.3",
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

            // Section 2: Progressive Step Milestones & Exchange Rate
            HelpGuideCard(
                title = "⚡ Progressive Step Goals & Exchange Rate",
                content = "• Official Exchange Rate: $1.00 USD = 100,000 Coins\n• Progressive Step Goals: Daily step rewards scale progressively across 7 levels starting from 50 steps up to 10,000 steps (50 -> 200 -> 500 -> 1,000 -> 2,500 -> 5,000 -> 10,000 steps).\n• 2x Double Rewards: Each step goal offers an optional 2x double coin boost via short rewarded video ads!\n• Active Workout Tracking: Start a workout session in the Activity tab for GPS route mapping, speed, distance, and real-time exercise stats."
            )

            // Section 3: Gamification & Bonus Center
            HelpGuideCard(
                title = "🎰 Gamification & Extra Ways to Earn",
                content = "StepPay offers exciting gamified features to multiply your earnings:\n• Lucky Spin Wheel: Get 3 Free Daily Spins + 10 Extra Ad Spins daily with sound effects to win up to +1,000 Coins per spin.\n• Virtual Sneakers Upgrade: Upgrade your sneakers from Level 1 to Level 5 to boost your step coin earnings by up to +25%!\n• Hydration Tracker: Log 8 glasses of water daily to claim a +500 Coins bonus.\n• Achievements & Badges: Unlock 5 unique milestone badges to claim instant coin bonuses.\n• Rate 5 Stars on Play Store: Rate StepPay 5 stars to claim +500 Coins.\n• Tapjoy & BitLabs Offerwalls: Play mobile games and answer surveys to earn thousands of coins!"
            )

            // Section 4: Referral & 10% Commission System
            HelpGuideCard(
                title = "👥 Invite Friends & 10% Lifetime Commission",
                content = "Invite your friends to join StepPay using your unique referral code:\n• Both you and your friend receive +10,000 Coins instantly when they sign up with your code.\n• You automatically earn 10% lifetime commission on all coins earned by friends you invited!\n• Share your link directly to WhatsApp, Instagram, or social media using the Invite Friends screen."
            )

            // Section 5: PayPal Cash Out & Anti-Fraud Mission
            HelpGuideCard(
                title = "💳 PayPal Cash Out & 7-Day Verification",
                content = "Redeem your coins directly for PayPal cash rewards from the Rewards screen:\n• $1.00 PayPal Cash = 100,000 Coins\n• $5.00 PayPal Cash = 500,000 Coins\n• $10.00 PayPal Cash = 1,000,000 Coins\n• $20.00 PayPal Cash = 2,000,000 Coins\n\n🛡️ Anti-Fraud Requirement: To prevent fake accounts, all payout requests require a 7-Day Daily Login Verification before cash is transferred to your PayPal email."
            )

            // Section 6: Anti-Cheat, Data Safety & Account Rights
            HelpGuideCard(
                title = "🛡️ Anti-Cheat & Account Deletion Rights",
                content = "• Fair Play: Phone shakers, fake GPS apps, emulators, or VPNs/proxies are strictly forbidden. Violators forfeit all coins.\n• Real-Time Firestore Sync: All user data and transactions are safely stored in Firebase Firestore.\n• Delete Account Right: You can permanently erase your account and all associated Firestore data at any time using the 'Delete Account' option in Settings or Profile."
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
