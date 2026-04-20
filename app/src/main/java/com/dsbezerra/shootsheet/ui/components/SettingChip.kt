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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dsbezerra.shootsheet.R
import com.dsbezerra.shootsheet.data.SettingKey
import com.dsbezerra.shootsheet.ui.icons.ShootSheetIcons
import com.dsbezerra.shootsheet.ui.theme.Accent
import com.dsbezerra.shootsheet.ui.theme.AccentDim
import com.dsbezerra.shootsheet.ui.theme.Border
import com.dsbezerra.shootsheet.ui.theme.Shapes
import com.dsbezerra.shootsheet.ui.theme.ShootSheetTextStyles
import com.dsbezerra.shootsheet.ui.theme.Surface3
import com.dsbezerra.shootsheet.ui.theme.TextMuted
import com.dsbezerra.shootsheet.ui.theme.spacing

@Composable
fun SettingChip(
  settingKey: SettingKey,
  value: String,
  highlighted: Boolean,
  onTap: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val spacing = MaterialTheme.spacing
  val labelColor = if (highlighted) Accent else TextMuted
  val chipBg = if (highlighted) AccentDim else Surface3
  val chipBorder = if (highlighted) Accent else Border

  Column(modifier = modifier.padding(bottom = 10.dp)) {
    Text(
      text = settingKey.getLabel(context).uppercase(),
      color = labelColor,
      style = ShootSheetTextStyles.settingLabel,
    )
    Spacer(Modifier.height(6.dp))
    Row(
      modifier = Modifier
        .clip(Shapes.chip)
        .border(1.dp, chipBorder, Shapes.chip)
        .background(chipBg)
        .clickable(onClick = onTap)
        .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = value,
        color = Accent,
        style = ShootSheetTextStyles.settingValue,
      )
      Spacer(Modifier.width(spacing.sm))
      Icon(
        imageVector = ShootSheetIcons.Info,
        contentDescription = stringResource(R.string.cd_info),
        tint = TextMuted,
        modifier = Modifier.size(14.dp),
      )
    }
  }
}
