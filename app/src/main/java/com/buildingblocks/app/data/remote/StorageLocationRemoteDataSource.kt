package com.buildingblocks.app.data.remote

import com.buildingblocks.app.data.remote.dto.StorageLocationDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageLocationRemoteDataSource @Inject constructor(
    private val client: SupabaseClient
) {
    private val table get() = client.postgrest["storage_locations"]

    suspend fun getLocationsSince(userId: String, since: String): List<StorageLocationDto> =
        table.select {
            filter {
                eq("user_id", userId)
                gte("updated_at", since)
            }
        }.decodeList()

    suspend fun upsertLocations(locations: List<StorageLocationDto>) {
        if (locations.isEmpty()) return
        table.upsert(locations)
    }
}
