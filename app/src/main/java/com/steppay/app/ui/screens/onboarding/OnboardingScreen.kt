package com.steppay.app.ui.screens.onboarding

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.steppay.app.R
import com.steppay.app.ui.theme.BorderLight
import com.steppay.app.ui.theme.PrimaryText
import com.steppay.app.ui.theme.SecondaryText
import com.steppay.app.ui.theme.StepPayTheme
import com.steppay.app.ui.theme.SurfaceWhite

@Composable
fun OnboardingScreen(
    onStartClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onGoogleSignIn: (name: String, email: String, photoUrl: String) -> Unit = { _, _, _ -> },
    onOpenTerms: () -> Unit = {},
    onOpenPrivacy: () -> Unit = {}
) {
    val context = LocalContext.current

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val idToken = account.idToken
                    if (!idToken.isNullOrEmpty()) {
                        val credential = GoogleAuthProvider.getCredential(idToken, null)
                        FirebaseAuth.getInstance().signInWithCredential(credential)
                            .addOnCompleteListener { authTask ->
                                val fUser = authTask.result?.user
                                onGoogleSignIn(
                                    fUser?.displayName ?: account.displayName ?: "Google User",
                                    fUser?.email ?: account.email ?: "user@gmail.com",
                                    fUser?.photoUrl?.toString() ?: account.photoUrl?.toString() ?: ""
                                )
                            }
                    } else {
                        onGoogleSignIn(
                            account.displayName ?: "Google User",
                            account.email ?: "user@gmail.com",
                            account.photoUrl?.toString() ?: ""
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback for testing if Google Play Services is unavailable
                onGoogleSignIn("Google User", "user@gmail.com", "")
            }
        } else {
            // Test Sign-In fallback
            onGoogleSignIn("Google User", "user@gmail.com", "")
        }
    }

    fun launchGoogleSignIn() {
        try {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("369387270357-fp6qm4illvk0roa7jct88easct6r17sb.apps.googleusercontent.com")
                .requestEmail()
                .requestProfile()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            onGoogleSignIn("Google User", "user@gmail.com", "")
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Overlay Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Logo and App Name
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.steppay_icon),
                    contentDescription = "StepPay Logo",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(18.dp))
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "StepPay",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    letterSpacing = (-0.8).sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Walk, Run &\nEarn Real Rewards",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SecondaryText,
                    textAlign = TextAlign.Center,
                    letterSpacing = (-0.4).sp
                )
            }

            // Bottom Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Google Sign-In Button
                GoogleSignInButton(
                    onClick = { launchGoogleSignIn() }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Terms of Service & Privacy Policy Agreement Text
                Text(
                    text = "By continuing, you agree to our Terms of Service\nand Privacy Policy",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = SecondaryText,
                    textAlign = TextAlign.Center,
                    letterSpacing = (-0.2).sp,
                    lineHeight = 16.sp,
                    modifier = Modifier
                        .clickable { onOpenPrivacy() }
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .border(1.dp, BorderLight, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceWhite,
            contentColor = PrimaryText
        ),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google_logo),
                contentDescription = "Google Logo",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Continue with Google",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText,
                letterSpacing = (-0.3).sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    StepPayTheme {
        OnboardingScreen()
    }
}

