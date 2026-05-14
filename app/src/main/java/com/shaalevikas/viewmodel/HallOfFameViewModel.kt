package com.shaalevikas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shaalevikas.data.model.HallOfFameEntry
import com.shaalevikas.repository.NeedsRepository
import com.shaalevikas.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class HallOfFameViewModel @Inject constructor(
    repository: NeedsRepository
) : ViewModel() {

    val hallOfFameState: StateFlow<UiState<List<HallOfFameEntry>>> = repository.observeHallOfFame()
        .map { result ->
            when (result) {
                is com.shaalevikas.utils.ResultState.Success -> UiState.Success(result.data)
                is com.shaalevikas.utils.ResultState.Error -> UiState.Error(result.message)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)
}
