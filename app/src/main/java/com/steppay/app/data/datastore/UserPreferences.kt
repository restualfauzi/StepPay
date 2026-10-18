package com.steppay.app.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class UserPreferences(private val context: Context) {

    private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    private val SELECTED_COUNTRY = stringPreferencesKey("selected_country")
    private val DISTANCE_UNIT = stringPreferencesKey("distance_unit")

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[ONBOARDING_COMPLETED] ?: false }

    val selectedCountry: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[SELECTED_COUNTRY] ?: "US" }

    val distanceUnit: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[DISTANCE_UNIT] ?: "KM" }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setSelectedCountry(countryCode: String) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_COUNTRY] = countryCode
        }
    }

    suspend fun setDistanceUnit(unitCode: String) {
        context.dataStore.edit { preferences ->
            preferences[DISTANCE_UNIT] = unitCode
        }
    }
}
