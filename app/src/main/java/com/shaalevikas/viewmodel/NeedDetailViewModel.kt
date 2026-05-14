package com.shaalevikas.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shaalevikas.data.model.NeedItem
import com.shaalevikas.data.model.Pledge
import com.shaalevikas.repository.NeedsRepository
import com.shaalevikas.utils.ResultState
import com.shaalevikas.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class NeedDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val needsRepository: NeedsRepository
) : ViewModel() {

    private val needId: String = checkNotNull(savedStateHandle["needId"])

    private val _pledgeState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val pledgeState: StateFlow<UiState<Unit>> = _pledgeState.asStateFlow()

    val needState: StateFlow<UiState<NeedItem?>> = needsRepository.observeNeed(needId)
        .map { result ->
            when (result) {
                is ResultState.Success -> UiState.Success(result.data)
                is ResultState.Error -> UiState.Error(result.message)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    val pledgesState: StateFlow<UiState<List<Pledge>>> = needsRepository.observePledges(needId)
        .map { result ->
            when (result) {
                is ResultState.Success -> UiState.Success(result.data)
                is ResultState.Error -> UiState.Error(result.message)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    fun submitPledge(amount: Double, note: String) {
        viewModelScope.launch {
            _pledgeState.value = UiState.Loading
            _pledgeState.value = when (val result = needsRepository.pledgeSupport(needId, amount, note)) {
                is ResultState.Success -> UiState.Success(Unit)
                is ResultState.Error -> UiState.Error(result.message)
            }
        }
    }

    fun resetPledgeState() {
        _pledgeState.value = UiState.Idle
    }
}
