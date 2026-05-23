package com.buildingblocks.app.data.local.dao

import androidx.room.*
import com.buildingblocks.app.data.local.entity.BuildLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BuildLogDao {

    @Query("SELECT * FROM build_logs WHERE setId = :setId ORDER BY createdAt DESC")
    fun getLogsForSet(setId: String): Flow<List<BuildLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: BuildLogEntity)

    @Update
    suspend fun updateLog(log: BuildLogEntity)
}
