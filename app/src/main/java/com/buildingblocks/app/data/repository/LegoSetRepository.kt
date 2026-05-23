package com.buildingblocks.app.data.repository

import com.buildingblocks.app.domain.model.LegoSet
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface LegoSetRepository {
    fun getAllSets(userId: UUID): Flow<List<LegoSet>>
    fun getBacklogSets(userId: UUID): Flow<List<LegoSet>>
    fun getSetById(id: UUID): Flow<LegoSet?>
    fun searchSets(userId: UUID, query: String): Flow<List<LegoSet>>
    fun getSetCount(userId: UUID): Flow<Int>
    fun getBacklogCount(userId: UUID): Flow<Int>
    fun getSealedCount(userId: UUID): Flow<Int>
    fun getOldestBacklogSet(userId: UUID): Flow<LegoSet?>
    fun getAllThemes(userId: UUID): Flow<List<String>>
    suspend fun saveSet(set: LegoSet)
    suspend fun deleteSet(id: UUID)
}
