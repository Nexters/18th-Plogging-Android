package com.plogging.ecorun.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.plogging.ecorun.data.model.PloggingKeys

@Dao
interface PloggingKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(remoteKey: List<PloggingKeys>)

    @Query("SELECT * FROM remote_keys WHERE ploggingId = :ploggingId")
    fun remoteKeysPloggingId(ploggingId: String): PloggingKeys?

    @Query("DELETE FROM remote_keys WHERE userId = :userId")
    fun clearRemoteKeys(userId: String)
}