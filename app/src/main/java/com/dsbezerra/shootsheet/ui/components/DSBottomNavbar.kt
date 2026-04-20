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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dsbezerra.shootsheet.R
import com.dsbezerra.shootsheet.ui.icons.ShootSheetIcons
import com.dsbezerra.shootsheet.ui.theme.Accent
import com.dsbezerra.shootsheet.ui.theme.Border
import com.dsbezerra.shootsheet.ui.theme.ShootSheetTextStyles
import com.dsbezerra.shootsheet.ui.theme.ShootSheetTheme
import com.dsbezerra.shootsheet.ui.theme.Surface
import com.dsbezerra.shootsheet.ui.theme.TextMuted
import com.dsbezerra.shootsheet.ui.theme.spacing

enum class NavTab { EXPLORE, FAVORITES }

@Composable
fun DSBottomNavbar(
  activeTab: NavTab,
  onTabSelected: (NavTab) -> Unit,
  modifier: Modifier = Modifier,
) {
  val spacing = MaterialTheme.spacing

  Column(modifier = modifier.background(Surface)) {
    HorizontalDivider(color = Border, thickness = 1.dp)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(vertical = spacing.sm),
    ) {
      NavTabItem(
        label = stringResource(R.string.tab_explore),
        icon = ShootSheetIcons.Explore,
        active = activeTab == NavTab.EXPLORE,
        onClick = { onTabSelected(NavTab.EXPLORE) },
        modifier = Modifier.weight(1f),
      )
      NavTabItem(
        label = stringResource(R.string.tab_favorites),
        icon = if (activeTab == NavTab.FAVORITES) ShootSheetIcons.Favorite else ShootSheetIcons.FavoriteOutline,
        active = activeTab == NavTab.FAVORITES,
        onClick = { onTabSelected(NavTab.FAVORITES) },
        modifier = Modifier.weight(1f),
      )
    }
  }
}

@Composable
private fun NavTabItem(
  label: String,
  icon: ImageVector,
  active: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val spacing = MaterialTheme.spacing
  val tint = if (active) Accent else TextMuted

  Column(
    modifier = modifier
      .clickable(onClick = onClick)
      .padding(vertical = spacing.xs),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(spacing.xs),
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = tint,
      modifier = Modifier.size(24.dp),
    )
    Text(
      text = label,
      color = tint,
      style = ShootSheetTextStyles.navLabel,
      fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
    )
  }
}

@Composable
fun DSNavigationRail(
  activeTab: NavTab,
  onTabSelected: (NavTab) -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(modifier = modifier.fillMaxHeight()) {
    Column(
      modifier = Modifier
        .fillMaxHeight()
        .background(Surface)
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(vertical = 8.dp, horizontal = 4.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      NavTabItem(
        label = stringResource(R.string.tab_explore),
        icon = ShootSheetIcons.Explore,
        active = activeTab == NavTab.EXPLORE,
        onClick = { onTabSelected(NavTab.EXPLORE) },
      )
      NavTabItem(
        label = stringResource(R.string.tab_favorites),
        icon = if (activeTab == NavTab.FAVORITES) ShootSheetIcons.Favorite else ShootSheetIcons.FavoriteOutline,
        active = activeTab == NavTab.FAVORITES,
        onClick = { onTabSelected(NavTab.FAVORITES) },
      )
    }
    VerticalDivider(color = Border, thickness = 1.dp)
  }
}

@Preview
@Composable
private fun PreviewDSBottomNavbar() {
  ShootSheetTheme {
    DSBottomNavbar(
      activeTab = NavTab.EXPLORE,
      onTabSelected = {},
    )
  }
}
