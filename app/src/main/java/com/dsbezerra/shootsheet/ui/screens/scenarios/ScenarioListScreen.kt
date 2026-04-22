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
package com.dsbezerra.shootsheet.ui.screens.scenarios

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dsbezerra.shootsheet.R
import com.dsbezerra.shootsheet.ui.components.DSScaffold
import com.dsbezerra.shootsheet.ui.components.ScenarioCard
import com.dsbezerra.shootsheet.ui.components.ScreenError
import com.dsbezerra.shootsheet.ui.components.ScreenLoading
import com.dsbezerra.shootsheet.ui.icons.ShootSheetIcons
import com.dsbezerra.shootsheet.ui.theme.ShootSheetTextStyles
import com.dsbezerra.shootsheet.ui.theme.TextMuted
import com.dsbezerra.shootsheet.ui.theme.TextPrimary
import com.dsbezerra.shootsheet.ui.theme.spacing
import com.dsbezerra.shootsheet.ui.utils.WindowWidthClass
import com.dsbezerra.shootsheet.ui.utils.windowWidthClass

@Composable
fun ScenarioListRoute(
    categoryId: String,
    onNavigateToScenario: (scenarioId: String, categoryId: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    vm: ScenarioListViewModel = viewModel(
        key = categoryId,
        factory = ScenarioListViewModel.factory(categoryId, LocalContext.current),
    ),
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(vm.effect) {
        vm.effect.collect { effect ->
            when (effect) {
                is ScenarioListEffect.NavigateToScenario -> onNavigateToScenario(
                    effect.scenarioId,
                    effect.categoryId
                )

                ScenarioListEffect.NavigateBack -> onBack()
            }
        }
    }

    ScenarioListScreen(state = state, onEvent = vm::onEvent, modifier = modifier)
}

@Composable
fun ScenarioListScreen(
    state: ScenarioListState,
    onEvent: (ScenarioListEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    val context = LocalContext.current
    val widthClass = windowWidthClass
    val isExpanded = widthClass == WindowWidthClass.Expanded

    if (state.isLoading) {
        ScreenLoading(modifier = modifier)
        return
    }

    val errorMsg = state.error
    if (errorMsg != null) {
        ScreenError(
            message = errorMsg,
            onRetry = { onEvent(ScenarioListEvent.OnRetry) },
            modifier = modifier,
        )
        return
    }

    DSScaffold(
        modifier = modifier,
        topBar = {
            Row(
                modifier = Modifier
                    .then(
                        if (isExpanded) Modifier.widthIn(max = 600.dp) else Modifier.fillMaxWidth(),
                    )
                    .padding(horizontal = 14.dp, vertical = spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = ShootSheetIcons.Back,
                    contentDescription = stringResource(R.string.cd_back),
                    tint = TextPrimary,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { onEvent(ScenarioListEvent.OnBackClick) },
                )
                Column(modifier = Modifier.padding(start = spacing.md)) {
                    Text(
                        text = stringResource(R.string.scenario_list_category_label),
                        color = TextMuted,
                        style = ShootSheetTextStyles.metaLabel,
                    )
                    Text(
                        text = state.category?.getLabel(context) ?: "",
                        color = TextPrimary,
                        style = ShootSheetTextStyles.listHeaderTitle,
                    )
                }
            }
        },
    ) {
        LazyColumn(
            modifier = if (isExpanded) Modifier.align(Alignment.TopCenter).widthIn(max = 600.dp) else Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                horizontal = spacing.screenHorizontal,
                vertical = spacing.sm,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(state.scenarios, key = { it.id }) { scenario ->
                ScenarioCard(
                    scenario = scenario,
                    categoryColor = state.category?.composeColor ?: Color.Gray,
                    isFavorite = scenario.id in state.favorites,
                    onToggleFavorite = { onEvent(ScenarioListEvent.OnFavoriteClick(scenario.id)) },
                    onClick = { onEvent(ScenarioListEvent.OnScenarioClick(scenario.id)) },
                )
            }
            item { Spacer(Modifier.height(spacing.sm)) }
        }
    }
}
