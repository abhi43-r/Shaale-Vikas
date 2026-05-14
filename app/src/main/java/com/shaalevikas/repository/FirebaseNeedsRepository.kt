package com.shaalevikas.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.shaalevikas.data.model.HallOfFameEntry
import com.shaalevikas.data.model.NeedItem
import com.shaalevikas.data.model.Pledge
import com.shaalevikas.data.model.UserProfile
import com.shaalevikas.data.remote.FirestoreCollections
import com.shaalevikas.utils.ResultState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseNeedsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storageRepository: StorageRepository
) : NeedsRepository {

    override fun observeNeeds(): Flow<ResultState<List<NeedItem>>> = callbackFlow {
        val registration = firestore.collection(FirestoreCollections.NEEDS)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> trySend(ResultState.Error(error.message ?: "Unable to load needs.", error))
                    snapshot != null -> trySend(ResultState.Success(snapshot.documents.mapNotNull { document ->
                        document.toObject(NeedItem::class.java)?.copy(id = document.id)
                    }))
                }
            }
        awaitClose { registration.remove() }
    }

    override fun observeNeed(needId: String): Flow<ResultState<NeedItem?>> = callbackFlow {
        val registration = firestore.collection(FirestoreCollections.NEEDS).document(needId)
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> trySend(ResultState.Error(error.message ?: "Unable to load need.", error))
                    snapshot != null -> trySend(ResultState.Success(snapshot.toObject(NeedItem::class.java)?.copy(id = snapshot.id)))
                }
            }
        awaitClose { registration.remove() }
    }

    override fun observePledges(needId: String): Flow<ResultState<List<Pledge>>> = callbackFlow {
        val registration = firestore.collection(FirestoreCollections.PLEDGES)
            .whereEqualTo("needId", needId)
            .orderBy("pledgedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> trySend(ResultState.Error(error.message ?: "Unable to load pledges.", error))
                    snapshot != null -> trySend(ResultState.Success(snapshot.documents.mapNotNull { document ->
                        document.toObject(Pledge::class.java)?.copy(id = document.id)
                    }))
                }
            }
        awaitClose { registration.remove() }
    }

    override fun observeHallOfFame(): Flow<ResultState<List<HallOfFameEntry>>> = callbackFlow {
        val registration = firestore.collection(FirestoreCollections.PLEDGES)
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> trySend(ResultState.Error(error.message ?: "Unable to load contributors.", error))
                    snapshot != null -> {
                        val entries = snapshot.documents
                            .mapNotNull { it.toObject(Pledge::class.java) }
                            .groupBy { it.userId }
                            .values
                            .map { pledges ->
                                HallOfFameEntry(
                                    userId = pledges.firstOrNull()?.userId.orEmpty(),
                                    donorName = pledges.firstOrNull()?.donorName.orEmpty(),
                                    donorEmail = pledges.firstOrNull()?.donorEmail.orEmpty(),
                                    totalContribution = pledges.sumOf { it.amount },
                                    pledgeCount = pledges.size
                                )
                            }
                            .sortedByDescending { it.totalContribution }
                        trySend(ResultState.Success(entries))
                    }
                }
            }
        awaitClose { registration.remove() }
    }

    override suspend fun refreshNeeds(): ResultState<Unit> = try {
        firestore.collection(FirestoreCollections.NEEDS).get(Source.SERVER).await()
        ResultState.Success(Unit)
    } catch (exception: Exception) {
        ResultState.Error(exception.message ?: "Refresh failed. Offline cache may still be available.", exception)
    }

    override suspend fun createNeed(need: NeedItem, heroImageUri: Uri?): ResultState<Unit> {
        return try {
            val docRef = firestore.collection(FirestoreCollections.NEEDS).document()
            val heroUrl = heroImageUri?.let { uri ->
                when (val upload = storageRepository.uploadNeedImage(docRef.id, "hero", uri)) {
                    is ResultState.Success -> upload.data
                    is ResultState.Error -> return upload
                }
            }
            val payload = need.copy(
                id = docRef.id,
                heroImageUrl = heroUrl ?: need.heroImageUrl,
                createdBy = auth.currentUser?.uid.orEmpty(),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            docRef.set(payload).await()
            ResultState.Success(Unit)
        } catch (exception: Exception) {
            ResultState.Error(exception.message ?: "Unable to create need.", exception)
        }
    }

    override suspend fun updateNeed(
        need: NeedItem,
        heroImageUri: Uri?,
        beforeImageUri: Uri?,
        afterImageUri: Uri?
    ): ResultState<Unit> {
        return try {
            val heroUrl = heroImageUri?.let { uri ->
                when (val upload = storageRepository.uploadNeedImage(need.id, "hero", uri)) {
                    is ResultState.Success -> upload.data
                    is ResultState.Error -> return upload
                }
            }
            val beforeUrl = beforeImageUri?.let { uri ->
                when (val upload = storageRepository.uploadNeedImage(need.id, "before", uri)) {
                    is ResultState.Success -> upload.data
                    is ResultState.Error -> return upload
                }
            }
            val afterUrl = afterImageUri?.let { uri ->
                when (val upload = storageRepository.uploadNeedImage(need.id, "after", uri)) {
                    is ResultState.Success -> upload.data
                    is ResultState.Error -> return upload
                }
            }
            val updatedNeed = need.copy(
                heroImageUrl = heroUrl ?: need.heroImageUrl,
                beforeImageUrl = beforeUrl ?: need.beforeImageUrl,
                afterImageUrl = afterUrl ?: need.afterImageUrl,
                updatedAt = System.currentTimeMillis()
            )
            firestore.collection(FirestoreCollections.NEEDS).document(need.id).set(updatedNeed).await()
            ResultState.Success(Unit)
        } catch (exception: Exception) {
            ResultState.Error(exception.message ?: "Unable to update need.", exception)
        }
    }

    override suspend fun deleteNeed(needId: String): ResultState<Unit> = try {
        firestore.collection(FirestoreCollections.NEEDS).document(needId).delete().await()
        ResultState.Success(Unit)
    } catch (exception: Exception) {
        ResultState.Error(exception.message ?: "Unable to delete need.", exception)
    }

    override suspend fun pledgeSupport(needId: String, amount: Double, note: String): ResultState<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return ResultState.Error("Please log in to pledge support.")
            val userSnapshot = firestore.collection(FirestoreCollections.USERS).document(userId).get().await()
            val profile = userSnapshot.toObject(UserProfile::class.java)
                ?: return ResultState.Error("Your profile was not found.")
            val pledgeRef = firestore.collection(FirestoreCollections.PLEDGES).document()

            firestore.runBatch { batch ->
                val needRef = firestore.collection(FirestoreCollections.NEEDS).document(needId)
                batch.set(
                    pledgeRef,
                    Pledge(
                        id = pledgeRef.id,
                        needId = needId,
                        userId = userId,
                        donorName = profile.name,
                        donorEmail = profile.email,
                        amount = amount,
                        note = note,
                        pledgedAt = System.currentTimeMillis()
                    )
                )
                batch.update(needRef, mapOf(
                    "amountCollected" to FieldValue.increment(amount),
                    "updatedAt" to System.currentTimeMillis()
                ))
            }.await()
            ResultState.Success(Unit)
        } catch (exception: Exception) {
            ResultState.Error(exception.message ?: "Unable to record pledge.", exception)
        }
    }
}
