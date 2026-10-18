package com.steppay.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.steppay.app.ui.theme.BorderLight
import com.steppay.app.ui.theme.PrimaryBlue
import com.steppay.app.ui.theme.PrimaryText
import com.steppay.app.ui.theme.SurfaceWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepPayTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    backgroundColor: Color = SurfaceWhite,
    contentColor: Color = PrimaryText,
    actions: @Composable () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor,
                    letterSpacing = (-0.4).sp
                )
            },
            navigationIcon = {
                if (onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = "Back",
                            tint = PrimaryBlue
                        )
                    }
                }
            },
            actions = { actions() },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = backgroundColor
            )
        )
        HorizontalDivider(
            color = BorderLight,
            thickness = 0.5.dp
        )
    }
}
