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
import com.dsbezerra.shootsheet.data.ShootSheetData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStreamReader

class ShootSheetRepository(private val context: Context) {

  private val json = Json {
    ignoreUnknownKeys = true
  }

  private var cachedData: ShootSheetData? = null

  suspend fun getShootSheetData(): ShootSheetData = withContext(Dispatchers.IO) {
    cachedData ?: loadFromAssets().also { cachedData = it }
  }

  suspend fun getCategoryLabels(): Map<String, String> = getShootSheetData().categories.associate { it.id to it.getLabel(context) }

  private fun loadFromAssets(): ShootSheetData {
    val inputStream = context.assets.open("shootsheet_data.json")
    val reader = InputStreamReader(inputStream)
    return try {
      json.decodeFromString<ShootSheetData>(reader.readText())
    } finally {
      reader.close()
    }
  }
}
