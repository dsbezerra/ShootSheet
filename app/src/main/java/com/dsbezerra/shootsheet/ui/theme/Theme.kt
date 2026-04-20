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

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

private val ShootSheetColorScheme = darkColorScheme(
  primary = Accent,
  onPrimary = Bg,
  secondary = AccentDim,
  onSecondary = TextPrimary,
  background = Bg,
  onBackground = TextPrimary,
  surface = Surface,
  onSurface = TextPrimary,
  surfaceVariant = Surface2,
  onSurfaceVariant = TextSub,
  outline = Border,
  error = ErrorRed,
)

@Composable
fun ShootSheetTheme(content: @Composable () -> Unit) {
  val widthDp = LocalConfiguration.current.screenWidthDp
  val spacing = when {
    widthDp < 600 -> Spacing()
    widthDp < 840 -> Spacing(screenHorizontal = 24.dp)
    else -> Spacing(screenHorizontal = 32.dp)
  }
  CompositionLocalProvider(LocalSpacing provides spacing) {
    MaterialTheme(
      colorScheme = ShootSheetColorScheme,
      typography = Typography,
      content = content,
    )
  }
}

/**
 * Access the spacing scale anywhere inside [ShootSheetTheme]:
 *   val spacing = MaterialTheme.spacing
 *   Modifier.padding(horizontal = spacing.screenHorizontal)
 */
val MaterialTheme.spacing: Spacing
  @Composable
  @ReadOnlyComposable
  get() = LocalSpacing.current
