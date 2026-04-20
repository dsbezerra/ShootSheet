/*
 * Designed and developed by 2026 dsbezerra (Diego Bezerra)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dsbezerra.shootsheet.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Singleton DataStore for favorites, scoped to the application Context.
 *
 * The `preferencesDataStore` delegate guarantees exactly one DataStore instance
 * per file name per process. The [SharedPreferencesMigration] moves any favorites
 * already saved under the old "shootsheet_prefs" SharedPreferences file on first
 * access so existing users don't lose their saved scenarios.
 */
private val Context.favoritesDataStore: DataStore<Preferences> by preferencesDataStore(
  name = "favorites",
  produceMigrations = { ctx -> listOf(SharedPreferencesMigration(ctx, "shootsheet_prefs")) },
)

class FavoritesRepository(
  context: Context,
) {
  // applicationContext prevents Activity/Fragment leaks
  private val dataStore: DataStore<Preferences> = context.applicationContext.favoritesDataStore

  /** Emits the current set of favorited scenario IDs, reacting to every write. */
  val favorites: Flow<Set<String>> =
    dataStore.data
      .catch { cause ->
        // Recover from I/O corruption; rethrow any programming errors.
        if (cause is IOException) emit(emptyPreferences()) else throw cause
      }.map { prefs -> prefs[FAVORITES_KEY] ?: emptySet() }

  /**
   * Atomically adds or removes [scenarioId] from favorites.
   * Must be called from a coroutine (suspend).
   */
  suspend fun toggle(scenarioId: String) {
    dataStore.edit { prefs ->
      val current = prefs[FAVORITES_KEY] ?: emptySet()
      prefs[FAVORITES_KEY] = if (scenarioId in current) current - scenarioId else current + scenarioId
    }
  }

  private companion object {
    val FAVORITES_KEY = stringSetPreferencesKey("favorites")
  }
}
