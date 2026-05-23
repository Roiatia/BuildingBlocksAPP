package com.buildingblocks.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LegoSetDto(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("lego_set_number") val legoSetNumber: String? = null,
    @SerialName("name") val name: String,
    @SerialName("theme") val theme: String? = null,
    @SerialName("year") val year: Int? = null,
    @SerialName("piece_count") val pieceCount: Int? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("status") val status: String,
    @SerialName("notes") val notes: String? = null,
    @SerialName("priority") val priority: String,
    @SerialName("difficulty") val difficulty: String? = null,
    @SerialName("storage_location_id") val storageLocationId: String? = null,
    @SerialName("purchased_at") val purchasedAt: String? = null,
    @SerialName("added_at") val addedAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("deleted_at") val deletedAt: String? = null
)
