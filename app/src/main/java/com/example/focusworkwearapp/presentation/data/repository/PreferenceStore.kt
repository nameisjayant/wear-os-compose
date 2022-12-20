package com.example.focusworkwearapp.presentation.data.repository


import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.lang.Exception
import javax.inject.Inject


class PreferenceStore @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    companion object {
        private val Context.datastore by preferencesDataStore("preference")
        val buttonEnable = booleanPreferencesKey("buttonEnabled")
        val startButtonEnabled = booleanPreferencesKey("startButtonEnabled")
        val timer = stringPreferencesKey("timer")
        val endTime = stringPreferencesKey("endTime")
        val isRunning = booleanPreferencesKey("isRunning")
        val title = stringPreferencesKey("title")
        val des = stringPreferencesKey("des")
    }

    suspend fun setBooleanPref(key: Preferences.Key<Boolean>, value: Boolean) = context
        .datastore.edit { preference ->
            preference[key] = value
        }

    suspend fun setLongPref(key: Preferences.Key<Long>, value: Long) = context
        .datastore.edit { preference ->
            preference[key] = value
        }

    suspend fun setStringPref(key: Preferences.Key<String>, value: String) = context
        .datastore.edit { preference ->
            preference[key] = value
        }


    fun getLongPref(key: Preferences.Key<Long>): Flow<Long> = context
        .datastore.data
        .catch {
            if (this is Exception) {
                emit(emptyPreferences())
            }
        }.map { preference ->
            val data = preference[key] ?: 0
            data
        }

    fun getStringPref(key: Preferences.Key<String>): Flow<String> = context
        .datastore.data
        .catch {
            if (this is Exception) {
                emit(emptyPreferences())
            }
        }.map { preference ->
            val data = preference[key] ?: "0"
            data
        }


    fun getBooleanPref(key: Preferences.Key<Boolean>): Flow<Boolean> = context
        .datastore.data
        .catch {
            if (this is Exception) {
                emit(emptyPreferences())
            }
        }.map { preference ->
            val data = preference[key] ?: false
            data
        }

}