package com.steppay.app.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object ActivityTracking : Screen("activity_tracking")
    data object Bonus : Screen("bonus")
    data object Rewards : Screen("rewards")
    data object Withdrawal : Screen("withdrawal")
    data object History : Screen("history")
    data object Profile : Screen("profile")
    data object Statistics : Screen("statistics")
    data object Leaderboard : Screen("leaderboard")
    data object Info : Screen("info")
    data object Settings : Screen("settings")
    data object PrivacyPolicy : Screen("privacy_policy")
    data object TermsOfService : Screen("terms_of_service")
}
