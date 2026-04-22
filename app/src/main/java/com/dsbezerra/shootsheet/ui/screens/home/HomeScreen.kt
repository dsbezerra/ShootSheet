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
package com.dsbezerra.shootsheet.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dsbezerra.shootsheet.R
import com.dsbezerra.shootsheet.ui.components.CategoryCard
import com.dsbezerra.shootsheet.ui.components.DSScaffold
import com.dsbezerra.shootsheet.ui.components.ScreenError
import com.dsbezerra.shootsheet.ui.components.ScreenLoading
import com.dsbezerra.shootsheet.ui.icons.ShootSheetIcons
import com.dsbezerra.shootsheet.ui.theme.Accent
import com.dsbezerra.shootsheet.ui.theme.Bg
import com.dsbezerra.shootsheet.ui.theme.Border
import com.dsbezerra.shootsheet.ui.theme.Shapes
import com.dsbezerra.shootsheet.ui.theme.ShootSheetTextStyles
import com.dsbezerra.shootsheet.ui.theme.Surface
import com.dsbezerra.shootsheet.ui.theme.TextMuted
import com.dsbezerra.shootsheet.ui.theme.TextPrimary
import com.dsbezerra.shootsheet.ui.theme.TextSub
import com.dsbezerra.shootsheet.ui.theme.spacing
import com.dsbezerra.shootsheet.ui.utils.WindowWidthClass
import com.dsbezerra.shootsheet.ui.utils.windowWidthClass

// ── Route ─────────────────────────────────────────────────────────────────────
// Owns the ViewModel, collects state/effects, bridges navigation to ShootSheetApp.

@Composable
fun HomeRoute(
  onNavigateToCategory: (String) -> Unit,
  modifier: Modifier = Modifier,
  vm: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
) {
  val state by vm.state.collectAsStateWithLifecycle()

  LaunchedEffect(vm.effect) {
    vm.effect.collect { effect ->
      when (effect) {
        is HomeEffect.NavigateToCategory -> onNavigateToCategory(effect.categoryId)
      }
    }
  }

  HomeScreen(state = state, onEvent = vm::onEvent, modifier = modifier)
}

// ── Screen ────────────────────────────────────────────────────────────────────
// Stateless renderer. Receives state + single onEvent callback.

@Composable
fun HomeScreen(
  state: HomeState,
  onEvent: (HomeEvent) -> Unit,
  modifier: Modifier = Modifier,
) {
  val spacing = MaterialTheme.spacing

  // Purely visual: drives the search field border color, not business state.
  var focused by remember { mutableStateOf(false) }

  if (state.isLoading) {
    ScreenLoading(modifier = modifier)
    return
  }

  val errorMsg = state.error
  if (errorMsg != null) {
    ScreenError(
      message = errorMsg,
      onRetry = { onEvent(HomeEvent.OnRetry) },
      modifier = modifier,
    )
    return
  }

  DSScaffold(
    modifier = modifier,
    topBar = {
      // ── Header ───────────────────────────────────────────────────────
      Column(modifier = Modifier.padding(horizontal = spacing.screenHorizontal, vertical = spacing.xxl)) {
        Text(
          text = stringResource(R.string.app_eyebrow),
          color = Accent,
          style = ShootSheetTextStyles.eyebrow,
        )
        Spacer(Modifier.height(spacing.sm))
        Text(
          text = stringResource(R.string.home_title),
          color = TextPrimary,
          style = ShootSheetTextStyles.screenTitle,
        )
        Spacer(Modifier.height(spacing.xs))
        Text(
          text = stringResource(R.string.home_subtitle),
          color = TextSub,
          style = ShootSheetTextStyles.screenSubtitle,
        )
      }

      // ── Search ───────────────────────────────────────────────────────
      Row(
        modifier = Modifier
          .padding(horizontal = spacing.screenHorizontal)
          .padding(bottom = spacing.md)
          .clip(Shapes.searchField)
          .background(Surface)
          .border(1.dp, if (focused) Accent else Border, Shapes.searchField)
          .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = ShootSheetIcons.Search,
          contentDescription = stringResource(R.string.cd_search),
          tint = TextMuted,
          modifier = Modifier.size(18.dp),
        )
        BasicTextField(
          value = state.searchQuery,
          onValueChange = { onEvent(HomeEvent.OnSearchQueryChanged(it)) },
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 10.dp)
            .onFocusChanged { focused = it.isFocused },
          singleLine = true,
          textStyle = ShootSheetTextStyles.cardTitle.copy(color = TextPrimary),
          cursorBrush = SolidColor(Accent),
          decorationBox = { inner ->
            Box {
              if (state.searchQuery.isEmpty()) {
                Text(
                  text = stringResource(R.string.search_placeholder),
                  color = TextMuted,
                  style = ShootSheetTextStyles.cardTitle,
                )
              }
              inner()
            }
          },
        )
        if (state.searchQuery.isNotEmpty()) {
          Icon(
            imageVector = ShootSheetIcons.Close,
            contentDescription = stringResource(R.string.cd_clear),
            tint = TextMuted,
            modifier = Modifier
              .size(16.dp)
              .clickable { onEvent(HomeEvent.OnSearchQueryChanged("")) },
          )
        }
      }

      // ── Section label ─────────────────────────────────────────────────
      Text(
        text = if (state.searchQuery.isNotBlank()) {
          pluralStringResource(R.plurals.search_results_count, state.filteredCategories.size, state.filteredCategories.size)
        } else {
          stringResource(R.string.section_categories)
        },
        color = TextSub,
        style = ShootSheetTextStyles.sectionLabel,
        modifier = Modifier.padding(horizontal = spacing.screenHorizontal),
      )
      Spacer(Modifier.height(spacing.lg))
    },
  ) {
    // ── Category grid ─────────────────────────────────────────────────
    if (state.filteredCategories.isEmpty()) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("\uD83D\uDD0D", style = ShootSheetTextStyles.screenTitle.copy(fontSize = ShootSheetTextStyles.screenTitle.fontSize * 1.3f))
          Spacer(Modifier.height(spacing.md))
          Text(
            text = stringResource(R.string.search_no_results, state.searchQuery),
            color = TextMuted,
            style = ShootSheetTextStyles.cardTitle,
          )
        }
      }
    } else {
      val gridColumns = when (windowWidthClass) {
        WindowWidthClass.Compact -> 2
        WindowWidthClass.Medium -> 3
        WindowWidthClass.Expanded -> 4
      }
      LazyVerticalGrid(
        columns = GridCells.Fixed(gridColumns),
        contentPadding = PaddingValues(horizontal = spacing.screenHorizontal, vertical = spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
      ) {
        items(state.filteredCategories, key = { it.id }) { cat ->
          CategoryCard(
            category = cat,
            scenarioCount = state.categoryScenarioCounts[cat.id] ?: 0,
            onClick = { onEvent(HomeEvent.OnCategoryClick(cat.id)) },
          )
        }
      }
    }
  }
}
