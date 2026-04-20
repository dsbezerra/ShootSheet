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

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dsbezerra.shootsheet.data.Category
import com.dsbezerra.shootsheet.data.repository.ShootSheetRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ── Event ────────────────────────────────────────────────────────────────────

sealed interface HomeEvent {
  data class OnSearchQueryChanged(
    val query: String,
  ) : HomeEvent

  data class OnCategoryClick(
    val categoryId: String,
  ) : HomeEvent

  data object OnRetry : HomeEvent
}

// ── State ────────────────────────────────────────────────────────────────────

data class HomeState(
  val searchQuery: String = "",
  val filteredCategories: List<Category> = emptyList(),
  val categoryScenarioCounts: Map<String, Int> = emptyMap(),
  val isLoading: Boolean = true,
  val error: String? = null,
)

// ── Effect ───────────────────────────────────────────────────────────────────

sealed interface HomeEffect {
  data class NavigateToCategory(
    val categoryId: String,
  ) : HomeEffect
}

// ── ViewModel ────────────────────────────────────────────────────────────────

class HomeViewModel(
  private val repository: ShootSheetRepository,
) : ViewModel() {
  private val _state = MutableStateFlow(HomeState())
  val state: StateFlow<HomeState> = _state.asStateFlow()

  private var allCategories: List<Category> = emptyList()
  private var categoryLabels: Map<String, String> = emptyMap()

  private val _effect = Channel<HomeEffect>(Channel.BUFFERED)
  val effect = _effect.receiveAsFlow()

  init {
    loadData()
  }

  private fun loadData() {
    _state.update { it.copy(isLoading = true, error = null) }
    viewModelScope.launch {
      try {
        val data = repository.getShootSheetData()
        allCategories = data.categories
        categoryLabels = repository.getCategoryLabels()
        val counts = data.scenarios.mapValues { it.value.size }
        _state.update {
          it.copy(
            filteredCategories = allCategories,
            categoryScenarioCounts = counts,
            isLoading = false,
          )
        }
      } catch (e: Exception) {
        _state.update { it.copy(isLoading = false, error = e.message) }
      }
    }
  }

  fun onEvent(event: HomeEvent) {
    when (event) {
      is HomeEvent.OnSearchQueryChanged -> {
        updateSearch(event.query)
      }

      is HomeEvent.OnCategoryClick -> {
        viewModelScope.launch {
          _effect.send(HomeEffect.NavigateToCategory(event.categoryId))
        }
      }

      HomeEvent.OnRetry -> loadData()
    }
  }

  private fun updateSearch(query: String) {
    val filtered =
      if (query.isBlank()) {
        allCategories
      } else {
        allCategories.filter { categoryLabels[it.id].orEmpty().contains(query, ignoreCase = true) }
      }
    _state.update { it.copy(searchQuery = query, filteredCategories = filtered) }
  }

  companion object {
    val Factory = viewModelFactory {
      initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
        HomeViewModel(ShootSheetRepository(app))
      }
    }
  }
}
