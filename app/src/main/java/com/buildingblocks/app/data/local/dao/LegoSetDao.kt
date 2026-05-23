package com.buildingblocks.app.data.local.dao

import androidx.room.*
import com.buildingblocks.app.data.local.entity.LegoSetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LegoSetDao {

    @Query("SELECT * FROM lego_sets WHERE userId = :userId AND deletedAt IS NULL ORDER BY addedAt DESC")
    fun getAllSets(userId: String): Flow<List<LegoSetEntity>>

    @Query("""
        SELECT * FROM lego_sets
        WHERE userId = :userId AND status IN (:statuses) AND deletedAt IS NULL
        ORDER BY addedAt ASC
    """)
    fun getSetsByStatuses(userId: String, statuses: List<String>): Flow<List<LegoSetEntity>>

    @Query("SELECT * FROM lego_sets WHERE id = :id AND deletedAt IS NULL")
    fun getSetById(id: String): Flow<LegoSetEntity?>

    @Query("""
        SELECT * FROM lego_sets
        WHERE userId = :userId
          AND (name LIKE '%' || :query || '%'
               OR legoSetNumber LIKE '%' || :query || '%'
               OR theme LIKE '%' || :query || '%')
          AND deletedAt IS NULL
        ORDER BY addedAt DESC
    """)
    fun searchSets(userId: String, query: String): Flow<List<LegoSetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: LegoSetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<LegoSetEntity>)

    @Update
    suspend fun updateSet(set: LegoSetEntity)

    @Query("UPDATE lego_sets SET deletedAt = :deletedAt, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDeleteSet(id: String, deletedAt: Long, updatedAt: Long)

    @Query("SELECT COUNT(*) FROM lego_sets WHERE userId = :userId AND deletedAt IS NULL")
    fun getSetCount(userId: String): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM lego_sets
        WHERE userId = :userId
          AND status IN ('SEALED','UNBUILT','IN_PROGRESS','MISSING_PARTS','WAITING_FOR_DISPLAY_SPACE')
          AND deletedAt IS NULL
    """)
    fun getBacklogCount(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM lego_sets WHERE userId = :userId AND status = 'SEALED' AND deletedAt IS NULL")
    fun getSealedCount(userId: String): Flow<Int>

    @Query("""
        SELECT * FROM lego_sets
        WHERE userId = :userId
          AND status IN ('SEALED','UNBUILT','IN_PROGRESS','MISSING_PARTS','WAITING_FOR_DISPLAY_SPACE')
          AND deletedAt IS NULL
        ORDER BY addedAt ASC
        LIMIT 1
    """)
    fun getOldestBacklogSet(userId: String): Flow<LegoSetEntity?>

    @Query("SELECT DISTINCT theme FROM lego_sets WHERE userId = :userId AND theme IS NOT NULL AND deletedAt IS NULL ORDER BY theme ASC")
    fun getAllThemes(userId: String): Flow<List<String>>

    @Query("SELECT * FROM lego_sets WHERE userId = :userId AND updatedAt >= :sinceEpochMilli")
    suspend fun getSetsSinceIncludeDeleted(userId: String, sinceEpochMilli: Long): List<LegoSetEntity>

    @Query("SELECT * FROM lego_sets WHERE id = :id LIMIT 1")
    suspend fun getSetByIdOnce(id: String): LegoSetEntity?
}
