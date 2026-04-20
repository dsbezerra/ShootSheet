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
package com.dsbezerra.shootsheet.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// M3 Typography — base styles consumed by MaterialTheme.
val Typography =
  Typography(
    bodyLarge =
    TextStyle(
      fontFamily = FontFamily.Default,
      fontWeight = FontWeight.Normal,
      fontSize = 16.sp,
      lineHeight = 24.sp,
      letterSpacing = 0.5.sp,
    ),
  )

/**
 * App-specific text styles that sit outside the M3 type scale.
 *
 * Usage:
 *   Text(text = "ShootSheet", style = ShootSheetTextStyles.eyebrow, color = Accent)
 */
object ShootSheetTextStyles {
  /** Uppercase eyebrow above screen titles — 11sp Medium, wide tracking. */
  val eyebrow =
    TextStyle(
      fontSize = 11.sp,
      fontWeight = FontWeight.Medium,
      letterSpacing = 1.sp,
    )

  /** Primary screen title — 24sp Bold. */
  val screenTitle =
    TextStyle(
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold,
    )

  /** Secondary screen subtitle / description — 13sp Regular. */
  val screenSubtitle = TextStyle(fontSize = 13.sp)

  /** Section heading ("Categories", "2 results") — 12sp SemiBold, wide tracking. */
  val sectionLabel =
    TextStyle(
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold,
      letterSpacing = 0.8.sp,
    )

  /** Scenario/list card primary title — 15sp SemiBold. */
  val cardTitle =
    TextStyle(
      fontSize = 15.sp,
      fontWeight = FontWeight.SemiBold,
    )

  /** Scenario/list card secondary subtitle — 13sp Regular. */
  val cardSubtitle = TextStyle(fontSize = 13.sp)

  /** Category card name — 14sp SemiBold. */
  val categoryLabel =
    TextStyle(
      fontSize = 14.sp,
      fontWeight = FontWeight.SemiBold,
    )

  /** Category card scenario count — 12sp Regular. */
  val categoryCount = TextStyle(fontSize = 12.sp)

  /** Mini chip (aperture/shutter/ISO) — 12sp SemiBold. */
  val chipValue =
    TextStyle(
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold,
    )

  /** Setting chip label (APERTURE, SHUTTER…) — 11sp Medium, tight tracking. */
  val settingLabel =
    TextStyle(
      fontSize = 11.sp,
      fontWeight = FontWeight.Medium,
      letterSpacing = 0.08.sp,
    )

  /** Setting chip value (f/1.8, 1/100…) — 15sp Bold. */
  val settingValue =
    TextStyle(
      fontSize = 15.sp,
      fontWeight = FontWeight.Bold,
    )

  /** General body copy — 14sp / 22sp line height. */
  val body =
    TextStyle(
      fontSize = 14.sp,
      lineHeight = 22.sp,
    )

  /** Tooltip / panel body — 14sp / 24sp line height. */
  val bodyRelaxed =
    TextStyle(
      fontSize = 14.sp,
      lineHeight = 24.sp,
    )

  /** Tooltip / panel title — 16sp SemiBold. */
  val panelTitle =
    TextStyle(
      fontSize = 16.sp,
      fontWeight = FontWeight.SemiBold,
    )

  /** Navigation tab label — 11sp Regular or SemiBold depending on active state. */
  val navLabel = TextStyle(fontSize = 11.sp)

  /** PRO TIP / badge-style labels — 12sp SemiBold, moderate tracking. */
  val badgeLabel =
    TextStyle(
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold,
      letterSpacing = 0.5.sp,
    )

  /** Screen list header meta text ("Category") — 12sp Regular. */
  val metaLabel = TextStyle(fontSize = 12.sp)

  /** Screen list header title (category name in ScenarioListScreen) — 20sp Bold. */
  val listHeaderTitle =
    TextStyle(
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
    )

  /** Empty-state headline — 18sp SemiBold. */
  val emptyTitle =
    TextStyle(
      fontSize = 18.sp,
      fontWeight = FontWeight.SemiBold,
    )

  /** Empty-state description — 14sp Regular. */
  val emptyBody = TextStyle(fontSize = 14.sp)
}
