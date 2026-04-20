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
package com.dsbezerra.shootsheet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dsbezerra.shootsheet.R
import com.dsbezerra.shootsheet.data.SettingKey
import com.dsbezerra.shootsheet.data.Tooltip
import com.dsbezerra.shootsheet.ui.icons.ShootSheetIcons
import com.dsbezerra.shootsheet.ui.theme.Accent
import com.dsbezerra.shootsheet.ui.theme.Shapes
import com.dsbezerra.shootsheet.ui.theme.ShootSheetTextStyles
import com.dsbezerra.shootsheet.ui.theme.Surface2
import com.dsbezerra.shootsheet.ui.theme.Surface3
import com.dsbezerra.shootsheet.ui.theme.TextSub
import com.dsbezerra.shootsheet.ui.theme.spacing

@Composable
fun TooltipSheet(
  settingKey: SettingKey,
  tooltips: Map<String, Tooltip>,
  onClose: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val tooltip = tooltips[settingKey.name] ?: return
  val context = LocalContext.current
  val spacing = MaterialTheme.spacing

  // Scrim
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.7f))
      .clickable(onClick = onClose),
    contentAlignment = Alignment.BottomCenter,
  ) {
    // Sheet
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(Shapes.bottomSheet)
        .background(Surface2)
        .clickable(enabled = false) {} // consume click so scrim doesn't fire
        .padding(horizontal = spacing.xl, vertical = spacing.xxl),
    ) {
      // Drag handle
      Box(
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .size(width = 36.dp, height = 4.dp)
          .background(Surface3, Shapes.chipSmall),
      )
      Spacer(Modifier.height(spacing.xl))

      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = tooltip.getTitle(context),
          color = Accent,
          style = ShootSheetTextStyles.panelTitle,
          modifier = Modifier.weight(1f),
        )
        Icon(
          imageVector = ShootSheetIcons.Close,
          contentDescription = stringResource(R.string.cd_close),
          tint = TextSub,
          modifier = Modifier
            .size(20.dp)
            .clickable(onClick = onClose),
        )
      }

      Spacer(Modifier.height(spacing.md))

      // Body
      Text(
        text = tooltip.getBody(context),
        color = TextSub,
        style = ShootSheetTextStyles.bodyRelaxed,
      )

      Spacer(Modifier.height(spacing.md))
    }
  }
}
