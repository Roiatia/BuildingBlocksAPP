package com.buildingblocks.app.data.remote

import com.buildingblocks.app.data.remote.dto.LegoSetDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LegoSetRemoteDataSource @Inject constructor(
    private val client: SupabaseClient
) {
    private val table get() = client.postgrest["lego_sets"]

    suspend fun getSetsSince(userId: String, since: String): List<LegoSetDto> =
        table.select {
            filter {
                eq("user_id", userId)
                gte("updated_at", since)
            }
        }.decodeList()

    suspend fun upsertSets(sets: List<LegoSetDto>) {
        if (sets.isEmpty()) return
        table.upsert(sets)
    }
}
