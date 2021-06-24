package com.plogging.ecorun.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Plogging(
    @SerializedName("_id") val id: String,
    @SerializedName("meta") val ploggingDetail: PloggingDetail,
    @SerializedName("trash_list") val trashList: List<Trash>
) : Serializable
