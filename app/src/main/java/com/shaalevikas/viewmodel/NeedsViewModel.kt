package com.shaalevikas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shaalevikas.data.model.NeedFilter
import com.shaalevikas.data.model.NeedItem
import com.shaalevikas.data.model.NeedSortOption
import com.shaalevikas.repository.NeedsRepository
import com.shaalevikas.utils.ResultState
import com.shaalevikas.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class NeedsViewModel @Inject constructor(
    private val needsRepository: NeedsRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(NeedFilter())
    val filter: StateFlow<NeedFilter> = _filter.asStateFlow()

    private val _refreshing = MutableStateFlow(false)
    val refreshing: StateFlow<Boolean> = _refreshing.asStateFlow()

    private val _refreshError = MutableStateFlow<String?>(null)
    val refreshError: StateFlow<String?> = _refreshError.asStateFlow()

    private val needsResult = needsRepository.observeNeeds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ResultState.Success(emptyList()))

    val needsState: StateFlow<UiState<List<NeedItem>>> = combine(needsResult, filter) { result, filterState ->
        when (result) {
            is ResultState.Success -> UiState.Success(applyFilter(result.data, filterState))
            is ResultState.Error -> UiState.Error(result.message)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    private fun applyFilter(items: List<NeedItem>, filter: NeedFilter): List<NeedItem> {
        val searched = items.filter { item ->
            val query = filter.query.trim()
            query.isBlank() || item.title.contains(query, true) || item.description.contains(query, true)
        }.filter { item ->
            filter.status == "All" || item.status.equals(filter.status, true)
        }
        return when (filter.sortOption) {
            NeedSortOption.LATEST -> searched.sortedByDescending { it.createdAt }
            NeedSortOption.HIGHEST_COST -> searched.sortedByDescending { it.estimatedCost }
            NeedSortOption.MOST_FUNDED -> searched.sortedByDescending { it.amountCollected }
            NeedSortOption.MOST_URGENT -> searched.sortedByDescending {
                when (it.priority.lowercase()) {
                    "critical" -> 3
                    "high" -> 2
                    else -> 1
                }
            }
        }
    }

    fun updateQuery(query: String) {
        _filter.value = _filter.value.copy(query = query)
    }

    fun updateSort(option: NeedSortOption) {
        _filter.value = _filter.value.copy(sortOption = option)
    }

    fun updateStatus(status: String) {
        _filter.value = _filter.value.copy(status = status)
    }

    fun refresh() {
        viewModelScope.launch {
            _refreshing.value = true
            _refreshError.value = null
            when (val result = needsRepository.refreshNeeds()) {
                is ResultState.Success -> Unit
                is ResultState.Error -> _refreshError.value = result.message
            }
            _refreshing.value = false
        }
    }
}
