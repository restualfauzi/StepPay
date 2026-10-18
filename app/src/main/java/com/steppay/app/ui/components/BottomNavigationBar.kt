package com.steppay.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.steppay.app.R
import com.steppay.app.ui.navigation.Screen
import com.steppay.app.ui.theme.BorderLight
import com.steppay.app.ui.theme.PrimaryBlue
import com.steppay.app.ui.theme.SecondaryText
import com.steppay.app.ui.theme.SurfaceWhite

data class NavigationItem(
    val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val screen: Screen
)

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit
) {
    val items = listOf(
        NavigationItem(R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home, Screen.Home),
        NavigationItem(R.string.nav_activity, Icons.AutoMirrored.Filled.DirectionsRun, Icons.AutoMirrored.Outlined.DirectionsRun, Screen.ActivityTracking),
        NavigationItem(R.string.nav_bonus, Icons.Filled.Stars, Icons.Outlined.Stars, Screen.Bonus),
        NavigationItem(R.string.nav_rewards, Icons.Filled.CardGiftcard, Icons.Outlined.CardGiftcard, Screen.Rewards),
        NavigationItem(R.string.nav_profile, Icons.Filled.Person, Icons.Outlined.Person, Screen.Profile)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceWhite.copy(alpha = 0.96f))
            .navigationBarsPadding()
    ) {
        // Ultra-thin iOS Top Separator
        HorizontalDivider(
            color = BorderLight,
            thickness = 0.5.dp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.screen.route
                val labelText = stringResource(id = item.titleRes)
                val color = if (isSelected) PrimaryBlue else SecondaryText

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(item.screen) }
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = labelText,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = labelText,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = color,
                        letterSpacing = (-0.2).sp
                    )
                }
            }
        }
    }
}
