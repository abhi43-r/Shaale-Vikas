package com.shaalevikas.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shaalevikas.data.model.AuthUser
import com.shaalevikas.data.model.UserProfile
import com.shaalevikas.data.model.UserRole
import com.shaalevikas.data.remote.FirestoreCollections
import com.shaalevikas.utils.ResultState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override val currentUser: AuthUser?
        get() = auth.currentUser?.let { AuthUser(uid = it.uid, email = it.email.orEmpty()) }

    override fun observeAuthState(): Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.let { AuthUser(uid = it.uid, email = it.email.orEmpty()) })
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun login(email: String, password: String): ResultState<Unit> = try {
        auth.signInWithEmailAndPassword(email, password).await()
        ResultState.Success(Unit)
    } catch (exception: Exception) {
        ResultState.Error(exception.message ?: "Login failed.", exception)
    }

    override suspend fun signup(
        name: String,
        email: String,
        password: String,
        role: UserRole
    ): ResultState<Unit> = try {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = authResult.user?.uid.orEmpty()
        val profile = UserProfile(
            id = uid,
            name = name,
            email = email,
            role = role.value
        )
        firestore.collection(FirestoreCollections.USERS).document(uid).set(profile).await()
        ResultState.Success(Unit)
    } catch (exception: Exception) {
        ResultState.Error(exception.message ?: "Signup failed.", exception)
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun getCurrentUserProfile(): ResultState<UserProfile?> {
        return try {
            val uid = auth.currentUser?.uid ?: return ResultState.Success(null)
            val snapshot = firestore.collection(FirestoreCollections.USERS).document(uid).get().await()
            ResultState.Success(snapshot.toObject(UserProfile::class.java))
        } catch (exception: Exception) {
            ResultState.Error(exception.message ?: "Unable to fetch profile.", exception)
        }
    }
}
