package com.shaalevikas.repository

import android.net.Uri
import com.shaalevikas.utils.ResultState

interface StorageRepository {
    suspend fun uploadNeedImage(needId: String, folder: String, uri: Uri): ResultState<String>
}
