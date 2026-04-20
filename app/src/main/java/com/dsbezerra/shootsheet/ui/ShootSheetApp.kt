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
package com.dsbezerra.shootsheet.ui

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.dsbezerra.shootsheet.ui.components.DSBottomNavbar
import com.dsbezerra.shootsheet.ui.components.DSNavigationRail
import com.dsbezerra.shootsheet.ui.components.NavTab
import com.dsbezerra.shootsheet.ui.mvi.ShootSheetEffect
import com.dsbezerra.shootsheet.ui.mvi.ShootSheetEvent
import com.dsbezerra.shootsheet.ui.navigation.Home
import com.dsbezerra.shootsheet.ui.navigation.ScenarioDetail
import com.dsbezerra.shootsheet.ui.navigation.ScenarioList
import com.dsbezerra.shootsheet.ui.screens.detail.DetailRoute
import com.dsbezerra.shootsheet.ui.screens.favorites.FavoritesRoute
import com.dsbezerra.shootsheet.ui.screens.home.HomeRoute
import com.dsbezerra.shootsheet.ui.screens.scenarios.ScenarioListRoute
import com.dsbezerra.shootsheet.ui.utils.WindowWidthClass
import com.dsbezerra.shootsheet.ui.utils.windowWidthClass

@Composable
fun ShootSheetApp(
  vm: ShootSheetViewModel = viewModel(),
  modifier: Modifier = Modifier,
) {
  val state by vm.state.collectAsStateWithLifecycle()
  val backStack = rememberNavBackStack(Home)

  LaunchedEffect(vm.effect) {
    vm.effect.collect { effect ->
      when (effect) {
        is ShootSheetEffect.NavigateToCategory ->
          backStack.add(ScenarioList(effect.categoryId))

        is ShootSheetEffect.NavigateToScenario ->
          backStack.add(ScenarioDetail(effect.scenarioId, effect.categoryId))

        ShootSheetEffect.NavigateBack ->
          backStack.removeLastOrNull()

        ShootSheetEffect.ClearBackStack ->
          while (backStack.size > 1) backStack.removeLastOrNull()
      }
    }
  }

  val showNav = backStack.lastOrNull() !is ScenarioDetail
  val isExpanded = windowWidthClass == WindowWidthClass.Expanded

  val navDisplay = @Composable { navModifier: Modifier ->
    NavDisplay(
      modifier = navModifier,
      backStack = backStack,
      onBack = { backStack.removeLastOrNull() },
      transitionSpec = {
        slideInHorizontally(initialOffsetX = { it }) togetherWith
          slideOutHorizontally(targetOffsetX = { -it })
      },
      popTransitionSpec = {
        slideInHorizontally(initialOffsetX = { -it }) togetherWith
          slideOutHorizontally(targetOffsetX = { it })
      },
      entryProvider = entryProvider {
        entry<Home> {
          when (state.activeTab) {
            NavTab.EXPLORE ->
              HomeRoute(
                onNavigateToCategory = {
                  vm.onEvent(
                    ShootSheetEvent.OnCategoryClick(
                      it,
                    ),
                  )
                },
              )

            NavTab.FAVORITES ->
              FavoritesRoute(
                onNavigateToScenario = { scenarioId, categoryId ->
                  vm.onEvent(
                    ShootSheetEvent.OnScenarioClick(
                      scenarioId,
                      categoryId,
                    ),
                  )
                },
              )
          }
        }
        entry<ScenarioList> {
          ScenarioListRoute(
            categoryId = it.categoryId,
            onNavigateToScenario = { scenarioId, categoryId ->
              vm.onEvent(ShootSheetEvent.OnScenarioClick(scenarioId, categoryId))
            },
            onBack = { vm.onEvent(ShootSheetEvent.OnBackClick) },
          )
        }
        entry<ScenarioDetail> {
          DetailRoute(
            scenarioId = it.scenarioId,
            categoryId = it.categoryId,
            onBack = { vm.onEvent(ShootSheetEvent.OnBackClick) },
          )
        }
      },
    )
  }

  if (isExpanded) {
    Row(modifier = modifier.fillMaxSize()) {
      if (showNav) {
        DSNavigationRail(
          activeTab = state.activeTab,
          onTabSelected = { vm.onEvent(ShootSheetEvent.OnTabSelected(it)) },
        )
      }
      navDisplay(Modifier.weight(1f))
    }
  } else {
    Column(modifier = modifier.fillMaxSize()) {
      navDisplay(Modifier.weight(1f))
      if (showNav) {
        DSBottomNavbar(
          activeTab = state.activeTab,
          onTabSelected = { vm.onEvent(ShootSheetEvent.OnTabSelected(it)) },
        )
      }
    }
  }
}
