package com.steppay.app.ui.screens.legal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.steppay.app.ui.components.StepPayTopBar
import com.steppay.app.ui.theme.BackgroundLight
import com.steppay.app.ui.theme.PrimaryBlue
import com.steppay.app.ui.theme.PrimaryText
import com.steppay.app.ui.theme.SecondaryText
import com.steppay.app.ui.theme.SurfaceWhite

@Composable
fun TermsOfServiceScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            StepPayTopBar(
                title = "Terms of Service",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "StepPay Terms of Service",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )

                    Text(
                        text = "Effective Date: October 2026\nDeveloper: Circle Mesh Limited\nApp Version: 0.0.1 (Early Access)\nSupport Contact: Support@circlemesh.site\n\nWelcome to StepPay. These Terms of Service (\"Terms\") constitute a legally binding agreement between you (\"User\") and Circle Mesh Limited (\"Company\", \"we\", or \"us\"). By downloading, installing, accessing, or using the StepPay mobile application, you agree to be bound by all of the terms and conditions contained herein.",
                        fontSize = 13.sp,
                        color = PrimaryText,
                        lineHeight = 18.sp
                    )

                    LegalSectionHeader("1. Eligibility & User Registration")
                    Text(
                        text = "• Age Limit: You must be at least 13 years of age (or the legal age of majority in your state/country) to use StepPay.\n• Account Ownership: You agree to register using a valid Google or Email account belonging to you. You are responsible for maintaining the confidentiality and activity of your account.\n• One Account Rule: Each physical user is allowed only ONE StepPay account. Registering or operating multiple accounts per person or device is strictly prohibited.",
                        fontSize = 13.sp,
                        color = SecondaryText,
                        lineHeight = 18.sp
                    )

                    LegalSectionHeader("2. Authentic Physical Activity & Anti-Fraud Policy")
                    Text(
                        text = "• Real Step Verification: StepPay rewards authentic human walking and running activities recorded via hardware pedometer sensors.\n• Progressive Step Milestones: Daily step rewards scale progressively across 7 milestone levels starting from 50 steps up to 10,000 steps (50 -> 200 -> 500 -> 1,000 -> 2,500 -> 5,000 -> 10,000 steps).\n• Prohibited Cheating Practices: Using mechanical phone shakers, automated step simulators, Android emulators, VPNs/proxies, GPS location spoofers, or reverse-engineered APKs is strictly forbidden.\n• Account Suspension & Forfeiture: Any detected attempt to generate artificial steps, manipulate coin balances, or bypass security checks will result in immediate account termination, blacklisting of device ID, and permanent forfeiture of all accumulated coins and pending payouts.",
                        fontSize = 13.sp,
                        color = SecondaryText,
                        lineHeight = 18.sp
                    )

                    LegalSectionHeader("3. StepPay Coins, Cash Redemption & 7-Day Login Mission")
                    Text(
                        text = "• Nature of Virtual Coins: StepPay Coins are non-monetary, in-app promotional tokens with an official exchange rate of $1.00 USD = 10,000 Coins. Coins do not constitute property or stored value until successfully redeemed.\n• Redemption Tiers: Users may request PayPal cash redemptions at designated conversion tiers:\n  - $1.00 PayPal Cash = 10,000 Coins\n  - $5.00 PayPal Cash = 50,000 Coins\n  - $10.00 PayPal Cash = 100,000 Coins\n  - $20.00 PayPal Cash = 200,000 Coins\n• Anti-Fraud 7-Day Login Streak Verification: To prevent automated bot requests and fake account fraud, all submitted payout requests require a 7-Day Daily Login Streak verification before final cash disbursement. Progress is tracked automatically upon daily app launch.\n• Payout Processing Time: Upon completing the 7-day login verification, payout requests undergo automated administrative review and are transferred within 24 to 48 business hours.\n• Account Accuracy: You are responsible for ensuring that your PayPal email address is entered correctly. StepPay is not responsible for funds sent to incorrectly entered email addresses.",
                        fontSize = 13.sp,
                        color = SecondaryText,
                        lineHeight = 18.sp
                    )

                    LegalSectionHeader("4. Third-Party Task Offers & Play Store Rating Mission")
                    Text(
                        text = "• Offerwall Partners: StepPay integrates third-party offerwall networks including Tapjoy and BitLabs.\n• Task Rewards: Users can earn bonus coins by completing game milestones or surveys. Rewards are issued upon validation from partner networks.\n• Play Store Rating Mission: Users can claim a +500 Coins bonus for rating StepPay 5 stars on Google Play Store.",
                        fontSize = 13.sp,
                        color = SecondaryText,
                        lineHeight = 18.sp
                    )

                    LegalSectionHeader("5. Health Disclaimer")
                    Text(
                        text = "StepPay provides physical activity tracking for informational, motivational, and fitness tracking purposes only. StepPay does not provide medical advice. Consult a healthcare professional before starting any new fitness program.",
                        fontSize = 13.sp,
                        color = SecondaryText,
                        lineHeight = 18.sp
                    )

                    LegalSectionHeader("6. Intellectual Property")
                    Text(
                        text = "All logos, trademarks, graphics, UI designs, source code, and original content in StepPay are the exclusive intellectual property of Circle Mesh Limited. Unauthorized reproduction, modification, or distribution is prohibited.",
                        fontSize = 13.sp,
                        color = SecondaryText,
                        lineHeight = 18.sp
                    )

                    LegalSectionHeader("7. Contact & Support")
                    Text(
                        text = "For support, legal inquiries, or account questions, please contact Circle Mesh Limited support team at Support@circlemesh.site.",
                        fontSize = 13.sp,
                        color = SecondaryText,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}
