package com.plogging.ecorun.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Trash(
    @SerializedName("pick_count")
    val pickCount: Int,
    @SerializedName("trash_type")
    val trashType: Int
) : Serializable