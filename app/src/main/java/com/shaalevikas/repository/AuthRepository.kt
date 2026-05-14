package com.shaalevikas.repository

import com.shaalevikas.data.model.AuthUser
import com.shaalevikas.data.model.UserProfile
import com.shaalevikas.data.model.UserRole
import com.shaalevikas.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: AuthUser?
    fun observeAuthState(): Flow<AuthUser?>
    suspend fun login(email: String, password: String): ResultState<Unit>
    suspend fun signup(name: String, email: String, password: String, role: UserRole): ResultState<Unit>
    suspend fun logout()
    suspend fun getCurrentUserProfile(): ResultState<UserProfile?>
}
