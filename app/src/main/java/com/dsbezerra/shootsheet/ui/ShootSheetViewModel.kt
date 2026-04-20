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

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dsbezerra.shootsheet.data.repository.FavoritesRepository
import com.dsbezerra.shootsheet.data.repository.ShootSheetRepository
import com.dsbezerra.shootsheet.ui.mvi.ShootSheetEffect
import com.dsbezerra.shootsheet.ui.mvi.ShootSheetEvent
import com.dsbezerra.shootsheet.ui.mvi.ShootSheetUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShootSheetViewModel(
  app: Application,
) : AndroidViewModel(app) {
  private val repository = ShootSheetRepository(app)
  private val favoritesRepository = FavoritesRepository(app)

  private val _state = MutableStateFlow(ShootSheetUiState())
  val state: StateFlow<ShootSheetUiState> = _state.asStateFlow()

  private val _effect = Channel<ShootSheetEffect>(Channel.BUFFERED)
  val effect = _effect.receiveAsFlow()

  private var categoryLabels: Map<String, String> = emptyMap()

  init {
    loadData()
    viewModelScope.launch {
      favoritesRepository.favorites.collect { favs ->
        _state.update { it.copy(favorites = favs) }
      }
    }
  }

  private fun loadData() {
    viewModelScope.launch {
      val data = repository.getShootSheetData()
      categoryLabels = repository.getCategoryLabels()
      _state.update {
        it.copy(
          data = data,
          isLoading = false,
          filteredCategories = data.categories,
        )
      }
    }
  }

  fun onEvent(event: ShootSheetEvent) = when (event) {
    is ShootSheetEvent.OnFavoriteClick -> {
      toggleFavorite(event.scenarioId)
    }

    is ShootSheetEvent.OnSearchQueryChanged -> {
      updateSearch(event.query)
    }

    is ShootSheetEvent.OnTabSelected -> {
      _state.update { it.copy(activeTab = event.tab) }
      _effect.trySend(ShootSheetEffect.ClearBackStack)
    }

    is ShootSheetEvent.OnCategoryClick -> {
      _effect.trySend(
        ShootSheetEffect.NavigateToCategory(
          event.categoryId,
        ),
      )
    }

    is ShootSheetEvent.OnScenarioClick -> {
      _effect.trySend(
        ShootSheetEffect.NavigateToScenario(
          event.scenarioId,
          event.categoryId,
        ),
      )
    }

    ShootSheetEvent.OnBackClick -> {
      _effect.trySend(ShootSheetEffect.NavigateBack)
    }
  }

  private fun toggleFavorite(id: String) {
    viewModelScope.launch { favoritesRepository.toggle(id) }
  }

  private fun updateSearch(query: String) {
    val allCategories = _state.value.data?.categories ?: emptyList()
    val filtered =
      if (query.isBlank()) {
        allCategories
      } else {
        allCategories.filter { categoryLabels[it.id].orEmpty().contains(query, ignoreCase = true) }
      }
    _state.update { it.copy(searchQuery = query, filteredCategories = filtered) }
  }
}
