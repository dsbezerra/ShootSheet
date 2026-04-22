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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dsbezerra.shootsheet.R
import com.dsbezerra.shootsheet.data.Scenario
import com.dsbezerra.shootsheet.ui.icons.ShootSheetIcons
import com.dsbezerra.shootsheet.ui.theme.Accent
import com.dsbezerra.shootsheet.ui.theme.Shapes
import com.dsbezerra.shootsheet.ui.theme.ShootSheetTextStyles
import com.dsbezerra.shootsheet.ui.theme.Surface
import com.dsbezerra.shootsheet.ui.theme.Surface2
import com.dsbezerra.shootsheet.ui.theme.TextPrimary
import com.dsbezerra.shootsheet.ui.theme.TextSub
import com.dsbezerra.shootsheet.ui.theme.spacing

@Composable
fun ScenarioCard(
  scenario: Scenario,
  categoryColor: Color,
  isFavorite: Boolean,
  onToggleFavorite: () -> Unit,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = androidx.compose.ui.platform.LocalContext.current
  val spacing = MaterialTheme.spacing

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(Shapes.card)
      .background(Surface)
      .clickable(onClick = onClick)
      .padding(spacing.lg),
  ) {
    Box(
      modifier = Modifier
        .align(Alignment.TopStart)
        .width(3.dp)
        .height(40.dp)
        .background(categoryColor, Shapes.chipSmall),
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Top,
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = scenario.getTitle(context),
          color = TextPrimary,
          style = ShootSheetTextStyles.cardTitle,
        )
        Spacer(Modifier.height(spacing.xxs))
        Text(
          text = scenario.getSubtitle(context),
          color = TextSub,
          style = ShootSheetTextStyles.cardSubtitle,
        )
        Row(
          modifier = Modifier.padding(top = spacing.lg),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          MiniChip(scenario.settings.aperture)
          MiniChip(scenario.settings.shutter)
          MiniChip(scenario.settings.iso)
        }
      }

      Icon(
        imageVector = if (isFavorite) ShootSheetIcons.Favorite else ShootSheetIcons.FavoriteOutline,
        contentDescription = stringResource(
          if (isFavorite) R.string.cd_remove_favorite else R.string.cd_add_favorite,
        ),
        tint = if (isFavorite) Accent else TextSub,
        modifier = Modifier
          .padding(top = spacing.xxs)
          .size(22.dp)
          .clickable(onClick = onToggleFavorite),
      )
    }
  }
}

@Composable
private fun MiniChip(value: String) {
  Box(
    modifier = Modifier
      .clip(Shapes.chipSmall)
      .background(Surface2)
      .padding(horizontal = 8.dp, vertical = 6.dp),
  ) {
    Text(
      text = value,
      color = Accent,
      style = ShootSheetTextStyles.chipValue,
    )
  }
}
