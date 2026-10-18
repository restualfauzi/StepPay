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
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            StepPayTopBar(
                title = "Privacy Policy",
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
                        text = "StepPay Privacy Policy",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )

                    Text(
                        text = "Effective Date: October 2026\nDeveloper: Circle Mesh Limited\nApp Version: 0.0.1 (Early Access)\nSupport Contact: Support@circlemesh.site\n\nAt Circle Mesh Limited (\"we\", \"our\", or \"us\"), we respect your privacy and are committed to protecting the personal data of users (\"you\") of the StepPay mobile application. This Privacy Policy describes how we collect, use, store, share, and protect your information when you download, install, or use StepPay, in compliance with Google Play Developer Policies and global data privacy laws.",
                        fontSize = 13.sp,
                        color = PrimaryText,
                        lineHeight = 18.sp
                    )

                    LegalSectionHeader("1. Information We Collect")
                    Text(
                        text = "• Account Information: When you sign in using Google Identity Services or Email, we collect your display name, email address, and profile photo URL.\n• Physical Activity Data: We request permission to access your device's physical activity sensors (ACTIVITY_RECOGNITION) to count steps, estimate distance walked/run, and calculate calories burned in real time.\n• Precise & Approximate Location Data: When you record an active workout, we request permission to access your GPS location (ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION) to render real-time workout maps and measure exact workout distance. Location data is collected only while a workout session is actively running.\n• Device & Identifiers: We collect device model, operating system version, system language, network status, and Android Advertising ID (AAID) for advertising, Tapjoy task verification, and anti-cheat fraud prevention.",
                        fontSize = 13.sp,
                        color = SecondaryText,
                        lineHeight = 18.sp
                    )

                    LegalSectionHeader("2. How We Use Your Information")
                    Text(
                        text = "• Core App Functionality: To track your steps, calculate health stats, and reward you with virtual StepPay Coins at the official exchange rate of $1.00 USD = 10,000 Coins.\n• Progressive Step Milestones & 2x Double Video Rewards: To verify daily step milestone progression starting from 50 steps up to 10,000 steps (50 -> 200 -> 500 -> 1,000 -> 2,500 -> 5,000 -> 10,000 steps) and reward base or 2x Double video claims.\n• Payout Processing & Anti-Fraud Verification: To process requested PayPal cash redemptions ($1 = 10,000 Coins, $5 = 50,000 Coins, $10 = 100,000 Coins, $20 = 200,000 Coins), verify recipient account details, and enforce the 7-Day Login Streak Anti-Fraud Verification requirement prior to cash disbursement.\n• Task & Review Rewards: To verify completion of Tapjoy game tasks, survey offers, and Google Play 5-Star Rating missions (+500 Coins).\n• Ad Personalization & Monetization: To serve mandatory rewarded video ads and interstitial ads prior to issuing mission rewards through Google AdMob.\n• Security & Anti-Fraud: To detect and prevent automated step shakers, GPS spoofers, emulators, VPNs/proxies, or multiple account abuse.",
                        fontSize = 13.sp,
                        color = SecondaryText,
                        lineHeight = 18.sp
                    )

                    LegalSectionHeader("3. Third-Party Services & Ad Networks")
                    Text(
                        text = "StepPay integrates third-party Software Development Kits (SDKs) that may collect or process data in accordance with their privacy policies:\n• Google Play Services & Firebase: For authentication, analytics, and Cloud Firestore synchronization.\n• Google AdMob: For serving personalized and non-personalized advertisements.\n• Tapjoy & BitLabs: For providing optional rewarded game offers, task rewards, and market research surveys.\nWe do not sell, rent, or trade your personal data to third parties for commercial marketing purposes.",
                        fontSize = 13.sp,
                        color = SecondaryText,
                        lineHeight = 18.sp
                    )

                    LegalSectionHeader("4. Data Security & Storage")
                    Text(
                        text = "Your personal data is encrypted both in transit (using HTTPS/TLS protocols) and at rest (using Firebase Cloud Firestore security features). Access to sensitive payout and account records is restricted strictly to authorized administrative staff.",
                        fontSize = 13.sp,
                        color = SecondaryText,
                        lineHeight = 18.sp
                    )

                    LegalSectionHeader("5. Children's Privacy (COPPA Compliance)")
                    Text(
                        text = "StepPay is not directed to children under the age of 13 (or 16 in certain EU jurisdictions). We do not knowingly collect or solicit personal information from children. If we discover that a child under the age limit has provided us with personal information, we will delete it immediately.",
                        fontSize = 13.sp,
                        color = SecondaryText,
                        lineHeight = 18.sp
                    )

                    LegalSectionHeader("6. User Rights & Account Deletion Policy")
                    Text(
                        text = "You have the right to access, update, or delete your personal data at any time. You can request complete deletion of your StepPay account and all associated records by:\n1. Emailing us at Support@circlemesh.site with the subject \"Account Deletion Request\".\n2. Using the account reset / delete option in the app Settings.\nUpon receiving your request, all personal data will be permanently erased within 14 business days.",
                        fontSize = 13.sp,
                        color = SecondaryText,
                        lineHeight = 18.sp
                    )

                    LegalSectionHeader("7. Updates to This Privacy Policy")
                    Text(
                        text = "Circle Mesh Limited may update this Privacy Policy from time to time to reflect changes in our practices or Google Play Developer Policy updates. Any changes will be posted within the app with an updated effective date.",
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

@Composable
fun LegalSectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = PrimaryText,
        modifier = Modifier.padding(top = 6.dp)
    )
}
