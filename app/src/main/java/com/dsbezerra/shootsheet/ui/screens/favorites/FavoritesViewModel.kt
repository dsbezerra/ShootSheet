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

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dsbezerra.shootsheet.data.ScenarioWithCategory
import com.dsbezerra.shootsheet.data.allScenariosWithCategory
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

sealed interface FavoritesEvent {
  data class OnFavoriteClick(
    val scenarioId: String,
  ) : FavoritesEvent

  data class OnScenarioClick(
    val scenarioId: String,
    val categoryId: String,
  ) : FavoritesEvent

  data object OnRetry : FavoritesEvent
}

// ── State ────────────────────────────────────────────────────────────────────

data class FavoritesState(
  val favorites: List<ScenarioWithCategory> = emptyList(),
  val isLoading: Boolean = true,
  val error: String? = null,
)

// ── Effect ───────────────────────────────────────────────────────────────────

sealed interface FavoritesEffect {
  data class NavigateToScenario(
    val scenarioId: String,
    val categoryId: String,
  ) : FavoritesEffect
}

// ── ViewModel ────────────────────────────────────────────────────────────────

class FavoritesViewModel(
  private val favoritesRepository: FavoritesRepository,
  private val shootSheetRepository: ShootSheetRepository,
) : ViewModel() {

  private val _scenariosState = MutableStateFlow<List<ScenarioWithCategory>>(emptyList())
  private val _isLoading = MutableStateFlow(true)
  private val _error = MutableStateFlow<String?>(null)

  val state: StateFlow<FavoritesState> =
    combine(
      _scenariosState,
      favoritesRepository.favorites,
      _isLoading,
      _error,
    ) { allScenarios, favorites, isLoading, error ->
      FavoritesState(
        favorites = allScenarios.filter { it.scenario.id in favorites },
        isLoading = isLoading,
        error = error,
      )
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = FavoritesState(),
    )

  private val _effect = Channel<FavoritesEffect>(Channel.BUFFERED)
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
        _scenariosState.value = data.allScenariosWithCategory()
        _isLoading.value = false
      } catch (e: Exception) {
        _isLoading.value = false
        _error.value = e.message
      }
    }
  }

  fun onEvent(event: FavoritesEvent) {
    when (event) {
      is FavoritesEvent.OnFavoriteClick -> {
        viewModelScope.launch { favoritesRepository.toggle(event.scenarioId) }
      }

      is FavoritesEvent.OnScenarioClick -> {
        viewModelScope.launch {
          _effect.send(FavoritesEffect.NavigateToScenario(event.scenarioId, event.categoryId))
        }
      }

      FavoritesEvent.OnRetry -> loadData()
    }
  }

  companion object {
    fun factory(context: Context) = viewModelFactory {
      initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
        FavoritesViewModel(FavoritesRepository(app), ShootSheetRepository(app))
      }
    }
  }
}
