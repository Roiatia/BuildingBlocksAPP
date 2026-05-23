package com.buildingblocks.app.data.repository

import com.buildingblocks.app.data.local.dao.LegoSetDao
import com.buildingblocks.app.data.local.entity.LegoSetEntity
import com.buildingblocks.app.domain.model.LegoSet
import com.buildingblocks.app.domain.model.SetStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class LegoSetRepositoryImpl @Inject constructor(
    private val dao: LegoSetDao
) : LegoSetRepository {

    private val backlogStatuses = SetStatus.entries
        .filter { it.isBacklog() }
        .map { it.name }

    override fun getAllSets(userId: UUID): Flow<List<LegoSet>> =
        dao.getAllSets(userId.toString()).map { it.map(LegoSetEntity::toDomain) }

    override fun getBacklogSets(userId: UUID): Flow<List<LegoSet>> =
        dao.getSetsByStatuses(userId.toString(), backlogStatuses)
            .map { it.map(LegoSetEntity::toDomain) }

    override fun getSetById(id: UUID): Flow<LegoSet?> =
        dao.getSetById(id.toString()).map { it?.toDomain() }

    override fun searchSets(userId: UUID, query: String): Flow<List<LegoSet>> =
        dao.searchSets(userId.toString(), query).map { it.map(LegoSetEntity::toDomain) }

    override fun getSetCount(userId: UUID): Flow<Int> = dao.getSetCount(userId.toString())

    override fun getBacklogCount(userId: UUID): Flow<Int> = dao.getBacklogCount(userId.toString())

    override fun getSealedCount(userId: UUID): Flow<Int> = dao.getSealedCount(userId.toString())

    override fun getOldestBacklogSet(userId: UUID): Flow<LegoSet?> =
        dao.getOldestBacklogSet(userId.toString()).map { it?.toDomain() }

    override fun getAllThemes(userId: UUID): Flow<List<String>> = dao.getAllThemes(userId.toString())

    override suspend fun saveSet(set: LegoSet) {
        dao.insertSet(LegoSetEntity.fromDomain(set))
    }

    override suspend fun deleteSet(id: UUID) {
        val now = Instant.now().toEpochMilli()
        dao.softDeleteSet(id.toString(), now, now)
    }
}
