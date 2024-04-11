// Import statements for necessary libraries and classes
package com.project.assign2.data.network.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.project.assign2.data.network.model.Auth
import kotlinx.coroutines.flow.map

// Class responsible for managing data storage using DataStore
class DataStoreManager(val context: Context) {

    // DataStore property delegated by preferencesDataStore extension function
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "AUTH")

    // Companion object holding keys for preferences
    companion object {
        // Keys for accessing stored password, Google authentication status, and email authentication status
        val PASSWORD = stringPreferencesKey("PASSWORD")
        val AUTH_GOOGLE = booleanPreferencesKey("AUTH_GOOGLE")
        val AUTH_EMAIL = booleanPreferencesKey("AUTH_EMAIL")
    }

    // Function to save data to DataStore
    suspend fun savetoDataStore(password: String, authGoogle: Boolean, authEmail: Boolean) {
        // Editing DataStore preferences using edit block
        context.dataStore.edit {
            // Storing password, Google authentication status, and email authentication status
            it[PASSWORD] = password
            it[AUTH_GOOGLE] = authGoogle
            it[AUTH_EMAIL] = authEmail
        }
    }

    // Flow to observe changes in authentication preferences
    val authPreference = context.dataStore.data.map {
        // Mapping stored preferences to Auth object
        Auth(
            it[PASSWORD] ?: "", // Retrieve stored password, default to empty string if not present
            it[AUTH_GOOGLE] ?: false, // Retrieve Google authentication status, default to false if not present
            it[AUTH_EMAIL] ?: false // Retrieve email authentication status, default to false if not present
        )
    }
}
