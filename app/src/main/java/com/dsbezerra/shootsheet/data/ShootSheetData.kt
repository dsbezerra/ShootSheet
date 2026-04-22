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
package com.dsbezerra.shootsheet.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import kotlinx.serialization.Serializable

// ── Models ─────────────────────────────────────────────────────────────────

@Serializable
data class Category(
  val id: String,
  val labelRes: String,
  val color: String, // Stored as Hex String in JSON
  val imageUrl: String? = null,
) {
  val composeColor: Color
    get() = try {
      Color(color.toColorInt())
    } catch (_: Exception) {
      Color.Gray
    }

  fun getLabel(context: Context): String = context.getStringResourceByName(labelRes)
}

@Serializable
data class ScenarioSettings(
  val aperture: String,
  val shutter: String,
  val iso: String,
  val wbRes: String,
  val focusRes: String,
) {
  fun getWb(context: Context): String = context.getStringResourceByName(wbRes)
  fun getFocus(context: Context): String = context.getStringResourceByName(focusRes)
}

@Serializable
data class Scenario(
  val id: String,
  val titleRes: String,
  val subtitleRes: String,
  val settings: ScenarioSettings,
  val tipRes: String,
  val categoryId: String,
) {
  fun getTitle(context: Context): String = context.getStringResourceByName(titleRes)
  fun getSubtitle(context: Context): String = context.getStringResourceByName(subtitleRes)
  fun getTip(context: Context): String = context.getStringResourceByName(tipRes)
}

@Serializable
data class Tooltip(val titleRes: String, val bodyRes: String) {
  fun getTitle(context: Context): String = context.getStringResourceByName(titleRes)
  fun getBody(context: Context): String = context.getStringResourceByName(bodyRes)
}

@Serializable
data class ShootSheetData(
  val categories: List<Category>,
  val scenarios: Map<String, List<Scenario>>,
  val tooltips: Map<String, Tooltip>,
)

enum class SettingKey(val labelRes: String) {
  APERTURE("setting_aperture"),
  SHUTTER("setting_shutter"),
  ISO("setting_iso"),
  WB("setting_wb"),
  FOCUS("setting_focus"),
  ;

  fun getLabel(context: Context): String = context.getStringResourceByName(labelRes)
}

data class ScenarioWithCategory(val scenario: Scenario, val category: Category)

fun ShootSheetData.allScenariosWithCategory(): List<ScenarioWithCategory> {
  return scenarios.flatMap { (catId, scenarioList) ->
    val category = categories.find { it.id == catId } ?: return@flatMap emptyList()
    scenarioList.map { ScenarioWithCategory(it, category) }
  }
}

// ── Helpers ────────────────────────────────────────────────────────────────

private fun Context.getStringResourceByName(name: String): String {
  val resId = resources.getIdentifier(name, "string", packageName)
  return if (resId != 0) getString(resId) else name
}
