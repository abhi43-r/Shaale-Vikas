package com.shaalevikas.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shaalevikas.data.model.NeedItem
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
class AdminNeedEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NeedsRepository
) : ViewModel() {

    private val needId: String? = savedStateHandle["needId"]

    private val _editorState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val editorState: StateFlow<UiState<Unit>> = _editorState.asStateFlow()

    val existingNeedState: StateFlow<UiState<NeedItem?>> =
        if (needId.isNullOrBlank()) {
            MutableStateFlow<UiState<NeedItem?>>(UiState.Success(null)).asStateFlow()
        } else {
            repository.observeNeed(needId)
                .map { result ->
                    when (result) {
                        is ResultState.Success -> UiState.Success(result.data)
                        is ResultState.Error -> UiState.Error(result.message)
                    }
                }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)
        }

    fun createNeed(need: NeedItem, heroImageUri: Uri?) {
        viewModelScope.launch {
            _editorState.value = UiState.Loading
            _editorState.value = when (val result = repository.createNeed(need, heroImageUri)) {
                is ResultState.Success -> UiState.Success(Unit)
                is ResultState.Error -> UiState.Error(result.message)
            }
        }
    }

    fun updateNeed(need: NeedItem, heroImageUri: Uri?, beforeImageUri: Uri?, afterImageUri: Uri?) {
        viewModelScope.launch {
            _editorState.value = UiState.Loading
            _editorState.value = when (val result = repository.updateNeed(need, heroImageUri, beforeImageUri, afterImageUri)) {
                is ResultState.Success -> UiState.Success(Unit)
                is ResultState.Error -> UiState.Error(result.message)
            }
        }
    }

    fun deleteNeed(needId: String) {
        viewModelScope.launch {
            _editorState.value = UiState.Loading
            _editorState.value = when (val result = repository.deleteNeed(needId)) {
                is ResultState.Success -> UiState.Success(Unit)
                is ResultState.Error -> UiState.Error(result.message)
            }
        }
    }

    fun clearState() {
        _editorState.value = UiState.Idle
    }
}
