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

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object Shapes {
  /** 6 dp — mini chips inside ScenarioCard. */
  val chipSmall = RoundedCornerShape(6.dp)

  /** 10 dp — full-size setting chips in DetailScreen. */
  val chip = RoundedCornerShape(10.dp)

  /** 12 dp — search input field. */
  val searchField = RoundedCornerShape(12.dp)

  /** 16 dp — category cards, scenario cards, detail sections. */
  val card = RoundedCornerShape(16.dp)

  /** 20 dp — small pill tags (e.g., category badge in detail). */
  val tag = RoundedCornerShape(20.dp)

  /** Top-only 20 dp — bottom sheet (TooltipSheet). */
  val bottomSheet = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
}
