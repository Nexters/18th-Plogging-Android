package com.plogging.ecorun.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class PloggingKeys(
    @PrimaryKey val ploggingId: String,
    val prevKey: Int?,
    val nextKey: Int?,
    val userId: String
)