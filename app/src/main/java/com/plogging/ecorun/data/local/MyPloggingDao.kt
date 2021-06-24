package com.plogging.ecorun.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.plogging.ecorun.data.model.MyDatabasePlogging

@Dao
interface MyPloggingDao {

    @Query("SELECT * FROM my_plogging WHERE userId = :userId ORDER BY :orderBy DESC")
    fun getAllPlogging(userId: String, orderBy: String): PagingSource<Int, MyDatabasePlogging>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(ploggingList: List<MyDatabasePlogging>)

    @Query("DELETE FROM my_plogging WHERE userId = :userId")
    fun clearPlogging(userId: String)
}