package com.steppay.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.steppay.app.ui.StepPayViewModel
import com.steppay.app.ui.components.RewardItem
import com.steppay.app.ui.screens.activity.ActivityTrackingScreen
import com.steppay.app.ui.screens.bonus.BonusScreen
import com.steppay.app.ui.screens.history.HistoryScreen
import com.steppay.app.ui.screens.home.HomeScreen
import com.steppay.app.ui.screens.info.InfoScreen
import com.steppay.app.ui.screens.leaderboard.LeaderboardScreen
import com.steppay.app.ui.screens.legal.PrivacyPolicyScreen
import com.steppay.app.ui.screens.legal.TermsOfServiceScreen
import com.steppay.app.ui.screens.onboarding.OnboardingScreen
import com.steppay.app.ui.screens.profile.ProfileScreen
import com.steppay.app.ui.screens.rewards.RewardsScreen
import com.steppay.app.ui.screens.settings.SettingsScreen
import com.steppay.app.ui.screens.splash.SplashScreen
import com.steppay.app.ui.screens.statistics.StatisticsScreen
import com.steppay.app.ui.screens.withdrawal.WithdrawalScreen

@Composable
fun StepPayNavHost(
    navController: NavHostController,
    viewModel: StepPayViewModel
) {
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    var selectedRewardItem by remember { mutableStateOf<RewardItem?>(null) }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it / 3 },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onStartClick = {
                    viewModel.completeOnboarding()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    viewModel.completeOnboarding()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onGoogleSignIn = { name, email, photoUrl ->
                    viewModel.signInWithGoogle(name, email, photoUrl)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onOpenTerms = { navController.navigate(Screen.TermsOfService.route) },
                onOpenPrivacy = { navController.navigate(Screen.PrivacyPolicy.route) }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigate = { screen ->
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.ActivityTracking.route) {
            ActivityTrackingScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Bonus.route) {
            BonusScreen(
                viewModel = viewModel,
                onNavigate = { screen ->
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Rewards.route) {
            RewardsScreen(
                viewModel = viewModel,
                onNavigate = { screen ->
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                    }
                },
                onRewardSelect = { reward ->
                    selectedRewardItem = reward
                    navController.navigate(Screen.Withdrawal.route)
                }
            )
        }

        composable(Screen.Withdrawal.route) {
            val reward = selectedRewardItem
            val initialAmountDollars = if (reward != null && reward.coinCost >= 10000) reward.coinCost / 10000 else 1
            WithdrawalScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onSuccess = {
                    navController.navigate(Screen.History.route) {
                        popUpTo(Screen.Withdrawal.route) { inclusive = true }
                    }
                },
                initialMethodName = reward?.title ?: "PayPal",
                initialAmount = initialAmountDollars
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                viewModel = viewModel,
                onNavigate = { screen ->
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = viewModel,
                onNavigate = { screen ->
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                    val isUserLoggedIn = isOnboardingCompleted == true || userProfile != null || firebaseUser != null
                    val destination = if (isUserLoggedIn) Screen.Home.route else Screen.Onboarding.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Info.route) {
            InfoScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.TermsOfService.route) {
            TermsOfServiceScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}