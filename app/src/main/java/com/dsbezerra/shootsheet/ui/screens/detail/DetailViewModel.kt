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

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dsbezerra.shootsheet.data.Category
import com.dsbezerra.shootsheet.data.Scenario
import com.dsbezerra.shootsheet.data.Tooltip
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

sealed interface DetailEvent {
    data object OnFavoriteClick : DetailEvent

    data object OnBackClick : DetailEvent

    data object OnRetry : DetailEvent
}

data class DetailState(
    val scenario: Scenario? = null,
    val category: Category? = null,
    val isFavorite: Boolean = false,
    val tooltips: Map<String, Tooltip> = emptyMap(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

sealed interface DetailEffect {
    data object NavigateBack : DetailEffect
}

typealias DetailDataState = Triple<Category?, Scenario?, Map<String, Tooltip>>

class DetailViewModel(
    private val scenarioId: String,
    private val categoryId: String,
    private val favoritesRepository: FavoritesRepository,
    private val shootSheetRepository: ShootSheetRepository,
) : ViewModel() {

    internal val dataState: StateFlow<DetailDataState>
        field = MutableStateFlow<DetailDataState>(DetailDataState(null, null, emptyMap()))

    internal val isLoading: StateFlow<Boolean>
        field = MutableStateFlow(true)

    internal val error: StateFlow<String?>
        field = MutableStateFlow<String?>(null)

    val state: StateFlow<DetailState> =
        combine(
            dataState,
            favoritesRepository.favorites,
            isLoading,
            error,
        ) { data, favorites, isLoading, error ->
            DetailState(
                scenario = data.second,
                category = data.first,
                isFavorite = scenarioId in favorites,
                tooltips = data.third,
                isLoading = isLoading,
                error = error,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DetailState(),
        )

    private val _effect = Channel<DetailEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadData()
    }

    private fun loadData() {
        isLoading.value = true
        error.value = null
        viewModelScope.launch {
            try {
                val data = shootSheetRepository.getShootSheetData()
                val category = data.categories.find { it.id == categoryId }
                val scenario = data.scenarios[categoryId]?.find { it.id == scenarioId }
                dataState.value = Triple(category, scenario, data.tooltips)
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                error.value = e.message
            }
        }
    }

    fun onEvent(event: DetailEvent) {
        when (event) {
            DetailEvent.OnFavoriteClick -> viewModelScope.launch {
                favoritesRepository.toggle(
                    scenarioId
                )
            }

            DetailEvent.OnBackClick -> viewModelScope.launch { _effect.send(DetailEffect.NavigateBack) }
            DetailEvent.OnRetry -> loadData()
        }
    }

    companion object {
        fun factory(
            scenarioId: String,
            categoryId: String,
            context: Context,
        ) = viewModelFactory {
            initializer {
                val app =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                DetailViewModel(
                    scenarioId = scenarioId,
                    categoryId = categoryId,
                    favoritesRepository = FavoritesRepository(app),
                    shootSheetRepository = ShootSheetRepository(app),
                )
            }
        }
    }
}
