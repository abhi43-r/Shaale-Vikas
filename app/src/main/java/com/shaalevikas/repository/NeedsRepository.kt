package com.shaalevikas.repository

import android.net.Uri
import com.shaalevikas.data.model.HallOfFameEntry
import com.shaalevikas.data.model.NeedItem
import com.shaalevikas.data.model.Pledge
import com.shaalevikas.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface NeedsRepository {
    fun observeNeeds(): Flow<ResultState<List<NeedItem>>>
    fun observeNeed(needId: String): Flow<ResultState<NeedItem?>>
    fun observePledges(needId: String): Flow<ResultState<List<Pledge>>>
    fun observeHallOfFame(): Flow<ResultState<List<HallOfFameEntry>>>
    suspend fun refreshNeeds(): ResultState<Unit>
    suspend fun createNeed(need: NeedItem, heroImageUri: Uri?): ResultState<Unit>
    suspend fun updateNeed(need: NeedItem, heroImageUri: Uri?, beforeImageUri: Uri?, afterImageUri: Uri?): ResultState<Unit>
    suspend fun deleteNeed(needId: String): ResultState<Unit>
    suspend fun pledgeSupport(needId: String, amount: Double, note: String): ResultState<Unit>
}
