package com.plogging.ecorun.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plogging.ecorun.data.model.Trash
import java.util.*

class TrashConverter {
    private val gson = Gson()

    @TypeConverter
    fun stringToList(data: String?): List<Trash> {
        data ?: return Collections.emptyList()
        val listType = object : TypeToken<List<Trash>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun listToString(someObjects: List<Trash>): String {
        return gson.toJson(someObjects)
    }
}