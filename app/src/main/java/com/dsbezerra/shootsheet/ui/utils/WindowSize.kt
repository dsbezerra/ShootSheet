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
package com.dsbezerra.shootsheet.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration

enum class WindowWidthClass { Compact, Medium, Expanded }

/**
 * Returns the current [WindowWidthClass] based on the screen width in dp.
 *
 * - Compact  : < 600 dp  (phones, portrait)
 * - Medium   : 600–839 dp (large phones, foldables unfolded, small tablets)
 * - Expanded : ≥ 840 dp  (tablets, landscape tablets)
 */
val windowWidthClass: WindowWidthClass
  @Composable
  @ReadOnlyComposable
  get() {
    val widthDp = LocalConfiguration.current.screenWidthDp
    return when {
      widthDp < 600 -> WindowWidthClass.Compact
      widthDp < 840 -> WindowWidthClass.Medium
      else -> WindowWidthClass.Expanded
    }
  }
