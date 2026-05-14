package com.shaalevikas.repository

import android.net.Uri
import com.shaalevikas.utils.ResultState
import javax.inject.Inject

class NoOpStorageRepository @Inject constructor() : StorageRepository {
    override suspend fun uploadNeedImage(needId: String, folder: String, uri: Uri): ResultState<String> {
        // Since storage is not enabled, we just return the local URI string or a placeholder.
        // In a real app without storage, you'd likely skip this or use another service.
        return ResultState.Success(uri.toString())
    }
}
