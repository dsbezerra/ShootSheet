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

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Spacing scale based on a 4 dp grid.
 *
 * Usage via the MaterialTheme extension:
 *   val spacing = MaterialTheme.spacing
 *   Modifier.padding(horizontal = spacing.screenHorizontal)
 */
@Immutable
data class Spacing(
  val xxs: Dp = 2.dp,
  val xs: Dp = 4.dp,
  val sm: Dp = 8.dp,
  val md: Dp = 12.dp,
  val lg: Dp = 16.dp,
  val xl: Dp = 20.dp,
  val xxl: Dp = 24.dp,
  /**
   * 18 dp — horizontal padding applied consistently at every screen edge.
   * Sits between [lg] (16) and [xl] (20) and is a deliberate design choice.
   */
  val screenHorizontal: Dp = 18.dp,
)

internal val LocalSpacing = staticCompositionLocalOf { Spacing() }
