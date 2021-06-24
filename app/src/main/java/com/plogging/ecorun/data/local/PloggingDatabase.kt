package com.plogging.ecorun.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.plogging.ecorun.data.model.MyDatabasePlogging
import com.plogging.ecorun.data.model.PloggingKeys
import com.plogging.ecorun.util.TrashConverter

@Database(
    entities = [MyDatabasePlogging::class, PloggingKeys::class],
    version = 7,
    exportSchema = false
)

@TypeConverters(TrashConverter::class)
abstract class PloggingDatabase : RoomDatabase() {
    abstract fun myPloggingDao(): MyPloggingDao
    abstract fun ploggingKeysDao(): PloggingKeysDao
}