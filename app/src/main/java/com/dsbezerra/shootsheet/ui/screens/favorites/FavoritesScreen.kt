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
package com.dsbezerra.shootsheet.ui.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dsbezerra.shootsheet.R
import com.dsbezerra.shootsheet.ui.components.DSHeader
import com.dsbezerra.shootsheet.ui.components.ScenarioCard
import com.dsbezerra.shootsheet.ui.components.ScreenError
import com.dsbezerra.shootsheet.ui.components.ScreenLoading
import com.dsbezerra.shootsheet.ui.icons.ShootSheetIcons
import com.dsbezerra.shootsheet.ui.theme.Bg
import com.dsbezerra.shootsheet.ui.theme.ShootSheetTextStyles
import com.dsbezerra.shootsheet.ui.theme.TextMuted
import com.dsbezerra.shootsheet.ui.theme.TextPrimary
import com.dsbezerra.shootsheet.ui.theme.TextSub
import com.dsbezerra.shootsheet.ui.theme.spacing
import com.dsbezerra.shootsheet.ui.utils.WindowWidthClass
import com.dsbezerra.shootsheet.ui.utils.windowWidthClass

@Composable
fun FavoritesRoute(
  onNavigateToScenario: (scenarioId: String, categoryId: String) -> Unit,
  modifier: Modifier = Modifier,
  vm: FavoritesViewModel = viewModel(factory = FavoritesViewModel.factory(LocalContext.current)),
) {
  val state by vm.state.collectAsStateWithLifecycle()

  LaunchedEffect(vm.effect) {
    vm.effect.collect { effect ->
      when (effect) {
        is FavoritesEffect.NavigateToScenario -> onNavigateToScenario(
          effect.scenarioId,
          effect.categoryId,
        )
      }
    }
  }

  FavoritesScreen(state = state, onEvent = vm::onEvent, modifier = modifier)
}

@Composable
fun FavoritesScreen(
  state: FavoritesState,
  onEvent: (FavoritesEvent) -> Unit,
  modifier: Modifier = Modifier,
) {
  val spacing = MaterialTheme.spacing
  val isExpanded = windowWidthClass == WindowWidthClass.Expanded

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Bg)
      .statusBarsPadding(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    if (state.isLoading) {
      ScreenLoading(modifier = modifier)
      return
    }

    val errorMsg = state.error
    if (errorMsg != null) {
      ScreenError(
        message = errorMsg,
        onRetry = { onEvent(FavoritesEvent.OnRetry) },
        modifier = modifier,
      )
      return
    }
    if (state.favorites.isEmpty()) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(horizontal = spacing.screenHorizontal),
        ) {
          Icon(
            imageVector = ShootSheetIcons.Camera,
            contentDescription = null,
            tint = TextMuted.copy(alpha = 0.25f),
            modifier = Modifier.size(48.dp),
          )
          Spacer(Modifier.height(spacing.lg))
          Text(
            text = stringResource(R.string.favorites_empty_title),
            color = TextPrimary,
            style = ShootSheetTextStyles.emptyTitle,
          )
          Spacer(Modifier.height(spacing.sm))
          Text(
            text = stringResource(R.string.favorites_empty_body),
            color = TextSub,
            style = ShootSheetTextStyles.emptyBody,
            textAlign = TextAlign.Center,
          )
        }
      }
    } else {
      DSHeader(
        title = stringResource(R.string.favorites_title),
        subtitle = pluralStringResource(
          id = R.plurals.favorites_saved_count,
          count = state.favorites.size,
          state.favorites.size,
        ),
        modifier = if (isExpanded) Modifier.widthIn(max = 600.dp) else Modifier.fillMaxWidth(),
      )
      LazyColumn(
        modifier = if (isExpanded) Modifier.widthIn(max = 600.dp) else Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
          horizontal = spacing.screenHorizontal,
          vertical = spacing.xs,
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        items(state.favorites, key = { it.scenario.id }) { (scenario, category) ->
          ScenarioCard(
            scenario = scenario,
            categoryColor = category.composeColor,
            isFavorite = true,
            onToggleFavorite = { onEvent(FavoritesEvent.OnFavoriteClick(scenario.id)) },
            onClick = {
              onEvent(
                FavoritesEvent.OnScenarioClick(
                  scenario.id,
                  category.id,
                ),
              )
            },
          )
        }
        item { Spacer(Modifier.height(spacing.sm)) }
      }
    }
  }
}
