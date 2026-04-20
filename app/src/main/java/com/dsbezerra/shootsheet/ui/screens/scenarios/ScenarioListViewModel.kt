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

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dsbezerra.shootsheet.data.Category
import com.dsbezerra.shootsheet.data.Scenario
import com.dsbezerra.shootsheet.data.repository.FavoritesRepository
import com.dsbezerra.shootsheet.data.repository.ShootSheetRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ── Event ────────────────────────────────────────────────────────────────────

sealed interface ScenarioListEvent {
  data class OnFavoriteClick(
    val scenarioId: String,
  ) : ScenarioListEvent

  data class OnScenarioClick(
    val scenarioId: String,
  ) : ScenarioListEvent

  data object OnBackClick : ScenarioListEvent

  data object OnRetry : ScenarioListEvent
}

// ── State ────────────────────────────────────────────────────────────────────

data class ScenarioListState(
  val category: Category? = null,
  val scenarios: List<Scenario> = emptyList(),
  val favorites: Set<String> = emptySet(),
  val isLoading: Boolean = true,
  val error: String? = null,
)

// ── Effect ───────────────────────────────────────────────────────────────────

sealed interface ScenarioListEffect {
  data class NavigateToScenario(
    val scenarioId: String,
    val categoryId: String,
  ) : ScenarioListEffect

  data object NavigateBack : ScenarioListEffect
}

// ── ViewModel ────────────────────────────────────────────────────────────────

class ScenarioListViewModel(
  private val categoryId: String,
  private val favoritesRepository: FavoritesRepository,
  private val shootSheetRepository: ShootSheetRepository,
) : ViewModel() {

  private val _dataState = MutableStateFlow<Pair<Category?, List<Scenario>>>(null to emptyList())
  private val _isLoading = MutableStateFlow(true)
  private val _error = MutableStateFlow<String?>(null)

  val state: StateFlow<ScenarioListState> =
    combine(
      _dataState,
      favoritesRepository.favorites,
      _isLoading,
      _error,
    ) { data, favorites, isLoading, error ->
      ScenarioListState(
        category = data.first,
        scenarios = data.second,
        favorites = favorites,
        isLoading = isLoading,
        error = error,
      )
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = ScenarioListState(),
    )

  private val _effect = Channel<ScenarioListEffect>(Channel.BUFFERED)
  val effect = _effect.receiveAsFlow()

  init {
    loadData()
  }

  private fun loadData() {
    _isLoading.value = true
    _error.value = null
    viewModelScope.launch {
      try {
        val data = shootSheetRepository.getShootSheetData()
        val category = data.categories.find { it.id == categoryId }
        val scenarios = data.scenarios[categoryId] ?: emptyList()
        _dataState.value = category to scenarios
        _isLoading.value = false
      } catch (e: Exception) {
        _isLoading.value = false
        _error.value = e.message
      }
    }
  }

  fun onEvent(event: ScenarioListEvent) {
    when (event) {
      is ScenarioListEvent.OnFavoriteClick -> {
        viewModelScope.launch { favoritesRepository.toggle(event.scenarioId) }
      }

      is ScenarioListEvent.OnScenarioClick -> {
        viewModelScope.launch {
          _effect.send(ScenarioListEffect.NavigateToScenario(event.scenarioId, categoryId))
        }
      }

      ScenarioListEvent.OnBackClick -> {
        viewModelScope.launch {
          _effect.send(ScenarioListEffect.NavigateBack)
        }
      }

      ScenarioListEvent.OnRetry -> loadData()
    }
  }

  companion object {
    fun factory(
      categoryId: String,
      context: Context,
    ) = viewModelFactory {
      initializer {
        ScenarioListViewModel(
          categoryId = categoryId,
          favoritesRepository = FavoritesRepository(context),
          shootSheetRepository = ShootSheetRepository(context),
        )
      }
    }
  }
}
