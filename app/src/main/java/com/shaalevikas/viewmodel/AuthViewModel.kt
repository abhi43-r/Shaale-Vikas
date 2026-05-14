package com.shaalevikas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shaalevikas.data.model.UserProfile
import com.shaalevikas.data.model.UserRole
import com.shaalevikas.repository.AuthRepository
import com.shaalevikas.utils.ResultState
import com.shaalevikas.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authActionState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val authActionState: StateFlow<UiState<Unit>> = _authActionState.asStateFlow()

    private val _profileState = MutableStateFlow<UiState<UserProfile?>>(UiState.Loading)
    val profileState: StateFlow<UiState<UserProfile?>> = _profileState.asStateFlow()

    val currentUser = authRepository.observeAuthState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), authRepository.currentUser)

    val isLoggedIn = currentUser
        .mapLatest { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), authRepository.currentUser != null)

    init {
        refreshProfile()
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authActionState.value = UiState.Loading
            _authActionState.value = when (val result = authRepository.login(email.trim(), password)) {
                is ResultState.Success -> {
                    refreshProfile()
                    UiState.Success(Unit)
                }
                is ResultState.Error -> UiState.Error(result.message)
            }
        }
    }

    fun signup(name: String, email: String, password: String, role: UserRole) {
        viewModelScope.launch {
            _authActionState.value = UiState.Loading
            _authActionState.value = when (val result = authRepository.signup(name.trim(), email.trim(), password, role)) {
                is ResultState.Success -> {
                    refreshProfile()
                    UiState.Success(Unit)
                }
                is ResultState.Error -> UiState.Error(result.message)
            }
        }
    }

    fun refreshProfile() {
        viewModelScope.launch {
            _profileState.value = UiState.Loading
            _profileState.value = when (val result = authRepository.getCurrentUserProfile()) {
                is ResultState.Success -> UiState.Success(result.data)
                is ResultState.Error -> UiState.Error(result.message)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _profileState.value = UiState.Success(null)
            _authActionState.value = UiState.Idle
        }
    }

    fun resetActionState() {
        _authActionState.value = UiState.Idle
    }
}
