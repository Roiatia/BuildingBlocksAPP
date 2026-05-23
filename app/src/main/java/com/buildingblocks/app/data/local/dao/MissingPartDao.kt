package com.buildingblocks.app.data.local.dao

import androidx.room.*
import com.buildingblocks.app.data.local.entity.MissingPartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MissingPartDao {

    @Query("SELECT * FROM missing_parts WHERE setId = :setId AND deletedAt IS NULL ORDER BY createdAt DESC")
    fun getPartsForSet(setId: String): Flow<List<MissingPartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPart(part: MissingPartEntity)

    @Update
    suspend fun updatePart(part: MissingPartEntity)

    @Query("UPDATE missing_parts SET deletedAt = :deletedAt, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDeletePart(id: String, deletedAt: Long, updatedAt: Long)
}
