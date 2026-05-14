package com.shaalevikas.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.shaalevikas.utils.ResultState
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FirebaseStorageRepository @Inject constructor(
    private val storage: FirebaseStorage
) : StorageRepository {

    override suspend fun uploadNeedImage(needId: String, folder: String, uri: Uri): ResultState<String> = try {
        val ref = storage.reference.child("needs/$needId/$folder/${UUID.randomUUID()}.jpg")
        ref.putFile(uri).await()
        ResultState.Success(ref.downloadUrl.await().toString())
    } catch (exception: Exception) {
        ResultState.Error(exception.message ?: "Image upload failed.", exception)
    }
}
