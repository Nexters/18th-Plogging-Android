package com.plogging.ecorun.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "my_plogging")
data class MyDatabasePlogging(
    @PrimaryKey val id: String,
    val calories: Int,
    val createdTime: String,
    val distance: Int,
    val ploggingActivityScore: Int,
    val ploggingEnvironmentScore: Int,
    val ploggingImg: String,
    val ploggingTime: Int,
    val ploggingTotalScore: Int,
    val userId: String,
    val trashList: List<Trash>,
    val trashSum: Int
) : Serializable