package uk.co.sullenart.panda2

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Settings(
    appContext: Context,
) {
    // Would be private but can't be because of inline function "fetch".
    val datastore: DataStore<Preferences> = PreferenceDataStoreFactory.create {
        appContext.preferencesDataStoreFile("settings")
    }

    fun exists(key: String): Flow<Boolean> {
        val prefKey = stringPreferencesKey(key)
        return datastore.data.map { it.contains(prefKey) }
    }

    suspend fun remove(key: String) {
        val prefKey = stringPreferencesKey(key)
        datastore.edit {
            it.remove(prefKey)
        }
    }

    suspend inline fun <reified T> store(key: String, value: T) {
        val prefKey = stringPreferencesKey(key)
        datastore.edit {
            it[prefKey] = Json.encodeToString(value)
        }
    }

    suspend inline fun <reified T> fetch(key: String): T? =
        try {
            val prefKey = stringPreferencesKey(key)
            val value = datastore.data.first()[prefKey]
            Json.decodeFromString(value.orEmpty())
        } catch (ignore: Exception) {
            null
        }
}