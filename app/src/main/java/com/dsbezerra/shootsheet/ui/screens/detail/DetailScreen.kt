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
package com.dsbezerra.shootsheet.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dsbezerra.shootsheet.R
import com.dsbezerra.shootsheet.data.Category
import com.dsbezerra.shootsheet.data.Scenario
import com.dsbezerra.shootsheet.data.SettingKey
import com.dsbezerra.shootsheet.ui.components.ScreenError
import com.dsbezerra.shootsheet.ui.components.ScreenLoading
import com.dsbezerra.shootsheet.ui.components.TooltipSheet
import com.dsbezerra.shootsheet.ui.icons.ShootSheetIcons
import com.dsbezerra.shootsheet.ui.theme.Accent
import com.dsbezerra.shootsheet.ui.theme.AccentDim
import com.dsbezerra.shootsheet.ui.theme.AccentDim2
import com.dsbezerra.shootsheet.ui.theme.Bg
import com.dsbezerra.shootsheet.ui.theme.Border
import com.dsbezerra.shootsheet.ui.theme.Shapes
import com.dsbezerra.shootsheet.ui.theme.ShootSheetTextStyles
import com.dsbezerra.shootsheet.ui.theme.Surface
import com.dsbezerra.shootsheet.ui.theme.TextMuted
import com.dsbezerra.shootsheet.ui.theme.TextPrimary
import com.dsbezerra.shootsheet.ui.theme.TextSub
import com.dsbezerra.shootsheet.ui.theme.spacing

// ── Route ─────────────────────────────────────────────────────────────────────
// Owns the ViewModel, collects state/effects, bridges navigation to ShootSheetApp.

@Composable
fun DetailRoute(
  scenarioId: String,
  categoryId: String,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
  vm: DetailViewModel = viewModel(
    key = scenarioId,
    factory = DetailViewModel.factory(scenarioId, categoryId, LocalContext.current),
  ),
) {
  val state by vm.state.collectAsStateWithLifecycle()

  LaunchedEffect(vm.effect) {
    vm.effect.collect { effect ->
      when (effect) {
        DetailEffect.NavigateBack -> onBack()
      }
    }
  }

  DetailScreen(state = state, onEvent = vm::onEvent, modifier = modifier)
}

// ── Screen ────────────────────────────────────────────────────────────────────
// Stateless renderer. Receives state + single onEvent callback.

@Composable
fun DetailScreen(
  state: DetailState,
  onEvent: (DetailEvent) -> Unit,
  modifier: Modifier = Modifier,
) {
  val scenario = state.scenario ?: return
  val category = state.category ?: return

  // Visual-only local state: drives grid highlight + tooltip overlay
  var highlighted by remember { mutableStateOf<SettingKey?>(null) }
  var tooltip by remember { mutableStateOf<SettingKey?>(null) }

  Box(modifier = modifier.fillMaxSize().background(Bg)) {
    if (state.isLoading) {
      ScreenLoading(modifier = modifier)
      return
    }

    val errorMsg = state.error
    if (errorMsg != null) {
      ScreenError(
        message = errorMsg,
        onRetry = { onEvent(DetailEvent.OnRetry) },
        modifier = modifier,
      )
      return
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .statusBarsPadding(),
    ) {
      DetailTopBar(
        isFavorite = state.isFavorite,
        onBack = { onEvent(DetailEvent.OnBackClick) },
        onFavorite = { onEvent(DetailEvent.OnFavoriteClick) },
      )
      DetailContentPanel(
        scenario = scenario,
        category = category,
        highlighted = highlighted,
        onChipTap = { key ->
          highlighted = key
          tooltip = key
        },
      )
    }

    // ── Tooltip overlay ─────────────────────────────────────────────────
    tooltip?.let { key ->
      TooltipSheet(
        settingKey = key,
        tooltips = state.tooltips,
        onClose = {
          tooltip = null
          highlighted = null
        },
      )
    }
  }
}

// ── Sub-composables ───────────────────────────────────────────────────────────

@Composable
private fun DetailTopBar(
  isFavorite: Boolean,
  onBack: () -> Unit,
  onFavorite: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val spacing = MaterialTheme.spacing
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = spacing.lg),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = ShootSheetIcons.Back,
      contentDescription = stringResource(R.string.cd_back),
      tint = TextPrimary,
      modifier = Modifier
        .size(22.dp)
        .clickable(onClick = onBack),
    )
    Spacer(Modifier.weight(1f))
    Icon(
      imageVector = if (isFavorite) ShootSheetIcons.Favorite else ShootSheetIcons.FavoriteOutline,
      contentDescription = stringResource(R.string.cd_toggle_favorite),
      tint = if (isFavorite) Accent else TextSub,
      modifier = Modifier
        .size(22.dp)
        .clickable(onClick = onFavorite),
    )
  }
}

@Composable
private fun DetailContentPanel(
  scenario: Scenario,
  category: Category,
  highlighted: SettingKey?,
  onChipTap: (SettingKey) -> Unit,
  modifier: Modifier = Modifier,
) {
  val spacing = MaterialTheme.spacing
  val context = LocalContext.current

  Column(modifier = modifier) {
    // Title block
    Column(modifier = Modifier.padding(horizontal = spacing.screenHorizontal, vertical = 10.dp)) {
      Box(
        modifier = Modifier
          .clip(Shapes.tag)
          .background(AccentDim)
          .padding(horizontal = spacing.md, vertical = spacing.xs),
      ) {
        Text(
          text = category.getLabel(context),
          color = Accent,
          style = ShootSheetTextStyles.chipValue,
        )
      }
      Spacer(Modifier.height(spacing.sm))
      Text(
        text = scenario.getTitle(context),
        color = TextPrimary,
        style = ShootSheetTextStyles.screenTitle,
      )
      Spacer(Modifier.height(spacing.xs))
      Text(
        text = scenario.getSubtitle(context),
        color = TextSub,
        style = ShootSheetTextStyles.screenSubtitle,
      )
    }

    // Settings block
    Column(
      modifier = Modifier
        .padding(horizontal = spacing.screenHorizontal)
        .padding(bottom = spacing.lg),
    ) {
      Text(
        text = stringResource(R.string.detail_camera_settings_title),
        color = Accent,
        style = ShootSheetTextStyles.badgeLabel,
      )
      Spacer(Modifier.height(14.dp))
      SettingsGrid(
        scenario = scenario,
        highlighted = highlighted,
        onTap = onChipTap,
      )
      Spacer(Modifier.height(spacing.xs))
      Text(
        text = stringResource(R.string.detail_camera_settings_hint),
        color = TextMuted,
        style = ShootSheetTextStyles.categoryCount,
      )
    }

    // Pro tip
    Row(
      modifier = Modifier
        .padding(horizontal = spacing.screenHorizontal)
        .padding(bottom = spacing.xxl)
        .clip(Shapes.card)
        .background(AccentDim2)
        .border(1.dp, Accent.copy(alpha = 0.2f), Shapes.card)
        .padding(spacing.lg),
    ) {
      Column {
        Text(
          text = stringResource(R.string.detail_pro_tip_label),
          color = Accent,
          style = ShootSheetTextStyles.badgeLabel,
        )
        Spacer(Modifier.height(6.dp))
        Text(
          text = scenario.getTip(context),
          color = TextPrimary,
          style = ShootSheetTextStyles.body,
        )
      }
    }
  }
}

@Composable
private fun SettingsGrid(
  scenario: Scenario,
  highlighted: SettingKey?,
  onTap: (SettingKey) -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current

  Column(
    modifier = modifier
      .clip(Shapes.card)
      .background(Surface)
      .border(1.dp, Border, Shapes.card),
  ) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
      SettingCell(
        settingKey = SettingKey.APERTURE,
        value = scenario.settings.aperture,
        highlighted = highlighted == SettingKey.APERTURE,
        onTap = { onTap(SettingKey.APERTURE) },
        modifier = Modifier.weight(1f),
      )
      VerticalDivider(thickness = 1.dp, color = Border)
      SettingCell(
        settingKey = SettingKey.SHUTTER,
        value = scenario.settings.shutter,
        highlighted = highlighted == SettingKey.SHUTTER,
        onTap = { onTap(SettingKey.SHUTTER) },
        modifier = Modifier.weight(1f),
      )
      VerticalDivider(thickness = 1.dp, color = Border)
      SettingCell(
        settingKey = SettingKey.ISO,
        value = scenario.settings.iso,
        highlighted = highlighted == SettingKey.ISO,
        onTap = { onTap(SettingKey.ISO) },
        modifier = Modifier.weight(1f),
      )
    }
    HorizontalDivider(thickness = 1.dp, color = Border)
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
      SettingCell(
        settingKey = SettingKey.WB,
        value = scenario.settings.getWb(context),
        highlighted = highlighted == SettingKey.WB,
        onTap = { onTap(SettingKey.WB) },
        modifier = Modifier.weight(1f),
      )
      VerticalDivider(thickness = 1.dp, color = Border)
      SettingCell(
        settingKey = SettingKey.FOCUS,
        value = scenario.settings.getFocus(context),
        highlighted = highlighted == SettingKey.FOCUS,
        onTap = { onTap(SettingKey.FOCUS) },
        modifier = Modifier.weight(1f),
      )
    }
  }
}

@Composable
private fun SettingCell(
  settingKey: SettingKey,
  value: String,
  highlighted: Boolean,
  onTap: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current

  Column(
    modifier = modifier
      .background(if (highlighted) AccentDim else Color.Transparent)
      .clickable(onClick = onTap)
      .padding(horizontal = 12.dp, vertical = 14.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = value,
      color = if (highlighted) Accent else TextPrimary,
      style = ShootSheetTextStyles.settingValue,
      textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(4.dp))
    Text(
      text = settingKey.getLabel(context).uppercase(),
      color = TextMuted,
      style = ShootSheetTextStyles.settingLabel,
      textAlign = TextAlign.Center,
    )
  }
}
