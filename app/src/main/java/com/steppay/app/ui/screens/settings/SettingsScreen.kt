package com.steppay.app.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.steppay.app.R
import com.steppay.app.domain.model.DistanceUnit
import com.steppay.app.domain.model.SupportedCountry
import com.steppay.app.ui.StepPayViewModel
import com.steppay.app.ui.components.PrimaryButton
import com.steppay.app.ui.components.StepPayTopBar
import com.steppay.app.ui.theme.BackgroundLight
import com.steppay.app.ui.theme.PrimaryText
import com.steppay.app.ui.theme.SecondaryText
import com.steppay.app.ui.theme.SurfaceWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: StepPayViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val currentCountryCode by viewModel.selectedCountry.collectAsState()
    val currentUnitCode by viewModel.distanceUnit.collectAsState()

    var name by remember(userProfile) { mutableStateOf(userProfile?.name ?: "Restu Alfauzi") }
    var dailyTargetStr by remember(userProfile) { mutableStateOf((userProfile?.dailyTargetSteps ?: 5000).toString()) }

    var selectedCountry by remember(currentCountryCode) { mutableStateOf(SupportedCountry.fromCode(currentCountryCode)) }
    var selectedUnit by remember(currentUnitCode) { mutableStateOf(DistanceUnit.fromCode(currentUnitCode)) }

    var countryExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            StepPayTopBar(
                title = stringResource(R.string.settings_title),
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
            // Section 1: User Profile Settings
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.user_profile),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(stringResource(R.string.user_name_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = dailyTargetStr,
                        onValueChange = { dailyTargetStr = it },
                        label = { Text(stringResource(R.string.daily_target_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }

            // Section 2: App Preferences (Country & Distance Unit)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.app_preferences),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Country Selector Dropdown
                    ExposedDropdownMenuBox(
                        expanded = countryExpanded,
                        onExpandedChange = { countryExpanded = !countryExpanded }
                    ) {
                        OutlinedTextField(
                            value = "${selectedCountry.flagEmoji} ${selectedCountry.name} (${selectedCountry.currencyCode})",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.country_label)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = countryExpanded,
                            onDismissRequest = { countryExpanded = false }
                        ) {
                            SupportedCountry.ALL.forEach { country ->
                                DropdownMenuItem(
                                    text = { Text("${country.flagEmoji} ${country.name} (${country.currencyCode})") },
                                    onClick = {
                                        selectedCountry = country
                                        countryExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Distance Unit Selector (KM vs Miles)
                    Text(
                        text = stringResource(R.string.distance_unit_label),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = SecondaryText
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        DistanceUnit.entries.forEach { unit ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { selectedUnit = unit }
                                    .padding(end = 16.dp, top = 4.dp)
                            ) {
                                RadioButton(
                                    selected = selectedUnit == unit,
                                    onClick = { selectedUnit = unit }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = unit.label,
                                    fontSize = 14.sp,
                                    color = PrimaryText
                                )
                            }
                        }
                    }
                }
            }

            // Save Action Button
            PrimaryButton(
                text = stringResource(R.string.save_changes),
                onClick = {
                    // Update ViewModel and DataStore
                    viewModel.updateProfileName(name)
                    viewModel.updateDailyTarget(dailyTargetStr.toIntOrNull() ?: 5000)
                    viewModel.setSelectedCountry(selectedCountry.code)
                    viewModel.setDistanceUnit(selectedUnit.code)

                    Toast.makeText(context, context.getString(R.string.preferences_saved), Toast.LENGTH_SHORT).show()
                    onBackClick()
                }
            )

            // Section 3: Danger Zone / Delete Account
            var showDeleteAccountDialog by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFFFFEBEE)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Danger Zone",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color(0xFFD32F2F)
                    )
                    Text(
                        text = "Permanently delete your account and remove all your data from Firebase Firestore & local storage.",
                        fontSize = 13.sp,
                        color = SecondaryText
                    )
                    
                    androidx.compose.material3.Button(
                        onClick = { showDeleteAccountDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = androidx.compose.ui.graphics.Color(0xFFD32F2F)
                        )
                    ) {
                        Text("Delete Account & Clear All Data", color = SurfaceWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (showDeleteAccountDialog) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { showDeleteAccountDialog = false },
                    containerColor = SurfaceWhite,
                    title = {
                        Text("Delete Account Permanently?", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color(0xFFD32F2F))
                    },
                    text = {
                        Text("This action CANNOT be undone. All your profile data, step records, coin balance, and transactions in Firebase Firestore will be erased permanently.", color = SecondaryText)
                    },
                    confirmButton = {
                        androidx.compose.material3.Button(
                            onClick = {
                                showDeleteAccountDialog = false
                                Toast.makeText(context, "Deleting account & data from Firestore...", Toast.LENGTH_LONG).show()
                                viewModel.deleteAccount {
                                    // Callback after deletion complete
                                }
                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = androidx.compose.ui.graphics.Color(0xFFD32F2F)
                            )
                        ) {
                            Text("Delete Permanently", color = SurfaceWhite, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        androidx.compose.material3.TextButton(onClick = { showDeleteAccountDialog = false }) {
                            Text("Cancel", color = SecondaryText)
                        }
                    }
                )
            }
        }
    }
}
